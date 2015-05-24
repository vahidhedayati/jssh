package grails.plugin.jssh


import grails.converters.JSON
import grails.plugin.jssh.j2ssh.J2sshService
import grails.plugin.jssh.j2ssh.JsshClientProcessService
import grails.plugin.jssh.j2ssh.JsshConfService
import grails.plugin.jssh.j2ssh.JsshMessagingService
import grails.plugin.jssh.j2ssh.JsshService
import grails.util.Environment

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/*
 * Vahid Hedayati
 * Jan 2015 - Major changes to Jssh WebSocket End Point
 * It now handles two types of websocket connections
 * The primary being older method which is a direct websocket connection to Jssh libraries.
 * 
 * It is probably confusing to try follow the logic but in short if there is a frontend
 * it is assumed the Endpoint call is being done via the new socket jssh:conn method and messages
 * are forwarded or redirected to jsshClientEndPoint.
 * There are exceptions such as /actions which some only new conn method calls and thus
 * calls are directed back to jsshClientEndpoint or its associate client classes.
 * 
 */
@WebListener
@ServerEndpoint("/j2ssh/{job}")
class J2sshEndpoint extends JsshConfService implements ServletContextListener {


	def jsshRandService

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private ConfigObject config1

	private JsshService jsshService
	private J2sshService j2sshService
	private JsshMessagingService jsshMessagingService
	private JsshClientProcessService jsshClientProcessService

	private String host, user, userpass, usercommand
	private int port = 22
	private boolean enablePong
	private int pingRate


	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {
			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(J2sshEndpoint)
			}

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)
			def grailsApplication = ctx.grailsApplication
			config1 = grailsApplication.config.jssh

			int defaultMaxSessionIdleTimeout = config1.timeout ?: 0
			serverContainer.defaultMaxSessionIdleTimeout = defaultMaxSessionIdleTimeout
		}
		catch (IOException e) {
			log.error e.message, e
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("job") String job) {

		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config1 = grailsApplication.config.jssh
		jsshService = ctx.jsshService
		j2sshService = ctx.j2sshService
		jsshMessagingService = ctx.jsshMessagingService
		jsshClientProcessService = ctx.jsshClientProcessService
		userSession.userProperties.put("job", job)
	}

	@OnMessage
	public void handleMessage(String message, Session userSession) {
		String username = userSession.userProperties.get("username") as String
		String job  =  userSession.userProperties.get("job") as String
		log.debug "@onMessage: $username: $job > $message\n\n"

		// Standard Private Messages
		if (message.startsWith('/pm')) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			if (user != username) {
				jsshMessagingService.privateMessage(userSession, user,msg,'/pm')
			}else{
				log.error "Messaging yourself?"
			}

			// NEW Sent only from FrontEnd when user triggers a command to be sent
		}else if  (message.startsWith('/fm')) {

			def values = parseInput("/fm ",message)
			String users = values.user as String
			String user
			if (users.indexOf('@')>-1) {
				user = users.substring(0,users.indexOf('@'))
			}else{
				user = users
			}
			String msg = values.msg as String
			if (msg.startsWith('{')) {
				jsshMessagingService.forwardMessage(user,msg)
			}else{
				jsshMessagingService.forwardMessage(user,"/bm $users,$msg")
			}
		}else if (message.startsWith('/bcast')) {
			def values = parseInput("/bcast ",message)
			String cjob = values.user as String
			String msg = values.msg as String
			jsshMessagingService.sendJobPM(userSession, cjob, msg)


			// All other actions
		}else{
			def data = JSON.parse(message)
			boolean bfrontend =  false
			if (data.frontend) {
				bfrontend =  data.frontend.toBoolean() ?: false
				if (!username) {
					String jsshUser = data.jsshUser
					if (jsshUser) {
						userSession.userProperties.put("username", jsshUser)
						sshUsers.putIfAbsent(jsshUser, 	userSession)
					}
				}
			}
			if (data) {
				if (bfrontend) {
					if (!data.client) {
						if  (data.DISCO == "true") {
							userSession.userProperties.put("status", "disconnect")
							jsshClientProcessService.handleClose(userSession)
						}else if (data.COMMAND == "true") {
							jsshMessagingService.sendBackPM(username, message,"system")
						}else{
							userSession.basicRemote.sendText("${message}")
						}
					}
				}else{
					if  (data.DISCO == "true") {
						jsshService.processRequest(userSession,'DISCO:-')		
						if (!destroySshUser(username)) {
							log.debug "failed to destroy: $username"
						}else{
							sleep(1000)
							if (userSession && userSession.isOpen()) {
								userSession.close()
							}
						}
						
					}else{
						String jsshUser = data.jsshUser ?: jsshRandService.randomise('jsshUser')
						userSession.userProperties.put("username", jsshUser)
						sshUsers.putIfAbsent(jsshUser, userSession)
						verifyGeneric(data)
						userSession.userProperties.put("pingRate", pingRate)
						// Initial call lets connect
						try  {
							def asyncProcess = new Thread({ jsshService.sshConnect(user, userpass, host, usercommand,  port, userSession) } as Runnable )
							asyncProcess.start()
						}catch (Exception e) {
						}


					}
				}
			} else{
				/* New websocket Client/Server Method
				 * Master (backend) does connection and processes commands sent via front-end
				 */
				if (bfrontend) {
					userSession.basicRemote.sendText("${message}")
				}
				// OLDER Websocket Method which does all processing via 1 connection
				else{
					jsshService.processRequest(userSession,message)
				}
			}
		}
	}

	private void verifyGeneric(JSONObject data) {
		this.host = data.hostname ?: ''

		if (data.port) {
			this.port = data.port.toInteger()
		}
		this.user = data.user ?: ''
		this.userpass = data.password ?: ''
		this.usercommand = data.usercommand ?: ''

		if (data.enablePong) {
			this.enablePong = data.enablePong?.toBoolean()
		}

		if (data.pingRate) {
			this.pingRate = data.pingRate?.toInteger() ?: '60000'
		}
	}


	@OnClose
	public void handeClose(Session userSession) {
		String username = userSession.userProperties.get("username") as String
		if (username) {
			if (username.endsWith(frontend2)) {
				userSession.userProperties.put("status", "disconnect")
				jsshClientProcessService.handleClose(userSession)
			}else{
				jsshService.processRequest(userSession,'DISCO:-')

			}
			if (!destroySshUser(username)) {
				log.debug "failed to destroy: $username"
			}else{
				sleep(1000)
				if (userSession && userSession.isOpen()) {
					userSession.close()
				}
			}
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}

	private String getFrontend2() {
		return config1?.frontenduser ?: '_frontend'
	}
}