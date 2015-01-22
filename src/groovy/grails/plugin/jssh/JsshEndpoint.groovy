package grails.plugin.jssh


import grails.converters.JSON
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

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient

/*
 * Vahid Hedayati
 * Jan 2015 - Major changes to Jssh WebSocket End Point
 * It now handles two types of websocket connections
 * The primary being older method which is a direct websocket connection to Jssh libraries.
 * 
 * Latter method has complicated things from my point of view since a lot of things has now broken due to this change
 * The change in short performs two socket connections 1 being front-end webpage 2nd being backend connection
 * 
 * The connection was initially done via taglib to the backend socket but was changed to allow interaction between the backend and frontend
 * The initial connection that is made via the tag lib passes the entire JSON string to EndPoint - 
 * EndPoint - in this segment: 
 *   Master (backend) does connection and processes commands sent via front-end
	  if (bfrontend) {
			userSession.basicRemote.sendText("${message}")
	  }
 * Around line 183 or so the forwards entire JSON to JsshClientEndPoint which is picked up by ClientProcessService
 * Right yes its crazy stuff but works  
 * 	  
 */
@WebListener
@ServerEndpoint("/j2ssh/{job}")
class JsshEndpoint extends ConfService implements ServletContextListener {


	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private ConfigObject config

	private AuthService authService
	private JsshService jsshService
	private J2sshService j2sshService
	private MessagingService messagingService

	private SshClient ssh = new SshClient()
	private SessionChannelClient session
	private SshConnectionProperties properties = null

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
				serverContainer.addEndpoint(JsshEndpoint)
			}

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)
			def grailsApplication = ctx.grailsApplication
			config = grailsApplication.config.jssh

			int defaultMaxSessionIdleTimeout = config.timeout ?: 0
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
		sshUsers.add(userSession)

		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config = grailsApplication.config.jssh
		authService = ctx.authService
		jsshService = ctx.jsshService
		j2sshService = ctx.j2sshService
		messagingService = ctx.messagingService
		userSession.userProperties.put("job", job)

	}

	@OnMessage
	public void handleMessage(String message, Session userSession) {
		String username = userSession.userProperties.get("username") as String
		String job  =  userSession.userProperties.get("job") as String
		if (config.debug == "on") {
			println "@onMessage: $message"
		}
		// Standard Private Messages
		if (message.startsWith('/pm')) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			if (user != username) {
				messagingService.privateMessage(userSession, user,msg,'/pm')
			}else{
				log.error "Messaging yourself?"
			}
			
		// NEW Sent only from FrontEnd when user triggers a command to be sent
		}else if  (message.startsWith('/fm')) {
			def values = parseInput("/fm ",message)
			String user = values.user as String
			String msg = values.msg as String
			if (msg.startsWith('{')) {
				messagingService.forwardMessage(user,msg)
			}else{
				messagingService.forwardMessage(user,"/bm $user,$msg")
			}
			
		
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
					}
				}
			}
			if (data) {
				if (bfrontend) {
					if (!data.client) {
						// 	Disconnect both the front-end and backend -
						if  (data.DISCO == "true") {
							messagingService.sendBackPM(username, message)
							userSession.close()
						}else{	
							userSession.basicRemote.sendText("${message}")
						}
					}
				}else{
					if  (data.DISCO == "true") {
						userSession.close()
					}else{	
						authService.authenticate(ssh, session, properties, userSession, data)
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
					jsshService.processRequest(ssh, session, properties, userSession,message)
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
	public void handeClose() {
		if (session && session.isOpen()) {
			session.close()
		}
		if (ssh && ssh.isConnected()) {
			ssh.disconnect()
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
		if (session && session.isOpen()) {
			session.close()
		}
		if (ssh && ssh.isConnected()) {
			ssh.disconnect()
		}
	}
}