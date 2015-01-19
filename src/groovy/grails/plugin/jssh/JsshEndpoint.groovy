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
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient

@WebListener
@ServerEndpoint("/j2ssh/{job}")
class JsshEndpoint implements ServletContextListener {
	static final Set<Session> sshUsers = ([] as Set).asSynchronized()

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private boolean enablePong = false
	private Integer pingRate
	private ConfigObject config
	private AuthService authService
	private JsshService jsshService

	private SshClient ssh = new SshClient()
	private SessionChannelClient session
	private SshConnectionProperties properties = null


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
		userSession.userProperties.put("job", job)

	}

	@OnMessage
	public void handleMessage(String message, Session userSession) {
		def data = JSON.parse(message)
		// authentication stuff - system calls
		if (data) {
			authService.authenticate(sshUsers, ssh, session, properties, userSession, data)
		} else{
			jsshService.processRequest(ssh, session, properties, userSession,message)
		}
	}

	@OnClose
	public void handeClose() {
		//log.debug "Client is now disconnected."
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
		if (session.isOpen()) {
			session.close()
		}
		if (ssh.isConnected()) {
			ssh.disconnect()
		}
	}




}