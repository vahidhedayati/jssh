package grails.plugin.jssh


import grails.converters.JSON
import grails.util.Environment
import groovy.time.TimeCategory

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.connection.ChannelOutputStream
import com.sshtools.j2ssh.connection.ChannelState
import com.sshtools.j2ssh.io.IOStreamConnector
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile

@WebListener
@ServerEndpoint("/j2ssh")
class JsshEndpoint implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private boolean enablePong = false
	private Integer pingRate
	private ConfigObject config

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

	String host, user, userpass, usercommand
	int port = 22
	StringBuilder output = new StringBuilder()


	private SshClient ssh = new SshClient()
	private SessionChannelClient session
	private SshConnectionProperties properties = null
	private boolean isAuthenticated = false
	private boolean pauseLog = false
	private boolean resumed = false
	private boolean newSession = false
	private boolean sameSession = false

	@OnOpen
	public String handleOpen(Session usersession) {
		usersession.basicRemote.sendText('Attempting SSH Connection')
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config = grailsApplication.config.jssh
	}


	@OnMessage
	public void handleMessage(String message, Session usersession) {
		def data = JSON.parse(message)
		// authentication stuff - system calls
		if (data) {
			host = data.hostname ?: ''

			if (data.port) {
				port = data.port.toInteger()
			}

			user = data.user ?: ''
			userpass = data.password ?: ''
			usercommand = data.usercommand ?: ''

			if (data.enablePong) {
				this.enablePong = data.enablePong.toBoolean()
			}
			if (data.pingRate) {
				this.pingRate = data.pingRate.toInteger()
			}
			
			// Initial call lets connect
			def asyncProcess = new Thread({sshConnect(user, userpass, host, usercommand, port, usersession)} as Runnable )
			asyncProcess.start()
			sleep(1000)
			def asyncProcess1 = new Thread({
				if (enablePong) {
					pingPong(usersession, pingRate)
			}
			} as Runnable )
			asyncProcess1.start()

		} else{
			if  (message.equals('DISCO:-')) {
				session.close()
				ssh.disconnect()
			} else if  (message.equals('PAUSE:-')) {
				pauseLog = true
			} else if  (message.equals('RESUME:-')) {
				pauseLog = false
				resumed = true
			} else if  (message.equals('NEW_SESSION:-')) {
				newSession = true
				sameSession = false
			} else if  (message.equals('NEW_SHELL:-')) {
				newShell(usersession)
				sameSession = true
			}else if (message.equals('CLOSE_SHELL:-')) {
				closeShell(usersession)
				sameSession = true
			} else if  (message.equals('SAME_SESSION:-')) {
				sameSession = true
				newSession = false
			}else if (message.equals('PONG')) {
				//log.debug "We have a pong"
				// Nothing to do - ping/pong controlled via user initial connector.
			} else {
				// Will return here conflicts with custom calls
				// Ensure user can actually send stuff according to back-end config
				if (config.security == "enabled")  {
					if ((!config.hideSendBlock)||(!config.hideSendBlock.equals('YES')))  {
						asyncProc(usersession,message)
					}
				}
				// No security just do it
				else{
					asyncProc(usersession,message)
				}
			}
		}
	}

	private void asyncProc(Session usersession, String message) {
		def asyncProcess = new Thread({sshControl(message, usersession)} as Runnable )
		asyncProcess.start()
	}

	@OnClose
	public void handeClose() {
		//log.debug "Client is now disconnected."
		session.close()
		ssh.disconnect()
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
		session.close()
		ssh.disconnect()
	}

	private void closeShell(Session usersession) {
		def myMsg = [:]
		int timeout = 1000;
		def cc = ssh.getActiveChannelCount() ?: 1
		if (cc>1) {
			session.close()
			session.getState().waitForState(ChannelState.CHANNEL_CLOSED, timeout);
			usersession.basicRemote.sendText('Shell closed')
		}else{
			usersession.basicRemote.sendText('Only 1 shell - could not close master window - try closing session : '+cc)
		}
		def ncc = ssh.getActiveChannelCount() ?: 1
		myMsg.put("connCount", ncc.toString())
		def myMsgj = myMsg as JSON
		usersession.basicRemote.sendText(myMsgj as String)
	}

	private void newShell(Session usersession) {
		session = ssh.openSessionChannel()

		SessionOutputReader sor = new SessionOutputReader(session)
		if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
			if (session.startShell()) {
				ChannelOutputStream out = session.getOutputStream()

			}
		}

		def cc = ssh.getActiveChannelCount() ?: 1
		def myMsg = [:]
		myMsg.put("connCount", cc.toString())
		def myMsgj = myMsg as JSON
		usersession.basicRemote.sendText(myMsgj as String)
		usersession.basicRemote.sendText('New shell created, console window : '+cc)
	}

	private void sshControl(String usercommand, Session usersession) {
		//StringBuilder catchup = new StringBuilder()
		Boolean newChann = false

		String newchannel = config.NEWCONNPERTRANS ?: ''
		String hideSessionCtrl = config.hideSessionCtrl ?: ''

		if (config.security == "enabled") {
			// Ensure user is not attempting to gain unauthorised access -
			// Check back-end config: ensure session control is enabled.
			if ((newchannel.equals('YES'))||(hideSessionCtrl.equals('NO')&&(newSession)&&(sameSession==false))) {
				newChann = true
			}else if ((newSession)&&(hideSessionCtrl.equals('NO'))) {
				newChann = true
			}else if ((sameSession)&&(hideSessionCtrl.equals('NO'))) {
				newChann = false
			}
		}
		// No security enabled - do as user asks.
		else{
			if ((newchannel.equals('YES'))||((newSession)&&(sameSession==false))) {
				newChann = true
			}else if (newSession) {
				newChann = true
			}else if (sameSession) {
				newChann = false
			}
		}

		def cc = ssh.getActiveChannelCount() ?: 1
		def myMsg = [:]
		myMsg.put("connCount", cc.toString())
		def myMsgj = myMsg as JSON
		usersession.basicRemote.sendText(myMsgj as String)
		if ((cc>1)&&(newChann==false)) {
			processConnection(usersession, usercommand)
		}else{
			session = ssh.openSessionChannel()
			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()
					processConnection(usersession, usercommand)
				}
			}
		}
		//session.close()
	}

	private void execCmd(String cmd, Session usersession) {
		try {
			session = ssh.openSessionChannel();
			if ( session.executeCommand(cmd) )	{
				IOStreamConnector output = new IOStreamConnector();
				java.io.ByteArrayOutputStream bos =  new
						java.io.ByteArrayOutputStream();
				output.connect(session.getInputStream(), bos );
				session.getState().waitForState(ChannelState.CHANNEL_CLOSED);
				usersession.basicRemote.sendText(bos.toString())
			}
		}	  catch(Exception e)  {
			log.debug "Exception : " + e.getMessage()
		}
	}

	private void sshConnect(String user, String userpass, String host, String usercommand, int port, Session usersession)  {

		String sshuser = config.USER ?: ''
		String sshpass = config.PASS ?: ''
		String sshkey = config.KEY ?: ''
		String sshkeypass = config.KEYPASS ?: ''
		String sshport = config.PORT ?: ''

		String username = user ?: sshuser
		String password = userpass ?: sshpass
		int sshPort = port ?: sshport as Integer
		String keyfilePass=''
		int result=0

		properties = new SshConnectionProperties();
		properties.setHost(host)
		properties.setPort(sshPort)
		ssh.connect(properties, new IgnoreHostKeyVerification())
		// User has a key authenticate using SSH Key
		if (!password) {
			PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient()
			pk.setUsername(username)

			SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(sshkey.toString()))
			if (file.isPassphraseProtected()) {
				keyfilePass = sshkeypass.toString()
			}
			SshPrivateKey key = file.toPrivateKey(keyfilePass);
			pk.setKey(key)
			// Try the authentication
			result = ssh.authenticate(pk)
			if (result == AuthenticationProtocolState.COMPLETE) {
				isAuthenticated = true
			}
		}
		// A password has been provided - attempt to ssh using password
		else{
			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient()
			pwd.setUsername(username)
			pwd.setPassword(password)
			result = ssh.authenticate(pwd)
			if(result == 4)  {
				isAuthenticated = true
			}
		}

		// Evaluate the result
		if (isAuthenticated) {
			sshControl(usercommand, usersession)
		}else{
			def authType="using key file  "
			if (password) { authType="using password" }
			usersession.basicRemote.sendText("SSH: Failed authentication user: ${username} on ${host} ${authType}")
		}
	}

	private void processConnection(Session usersession, String usercommand) {
		StringBuilder catchup = new StringBuilder()
		
		if (!usercommand.endsWith('\n')) {
			usercommand=usercommand+'\n'
		}

		session.getOutputStream().write(usercommand.getBytes())
		InputStream input = session.getInputStream()
		byte[] buffer = new byte[255]
		int read;
		int i=0
		if (usersession && usersession.isOpen()) {
			while ((read = input.read(buffer))>0) {
				String out1 = new String(buffer, 0, read)
				if (pauseLog) {
					catchup.append(out1)
				}else{
					if (resumed) {
						resumed = false
						usersession.basicRemote.sendText(parseBash(catchup as String))
						catchup = new StringBuilder()
					}
					usersession.basicRemote.sendText(parseBash(out1))
				}
			}
		}
	}

	private String parseBash(String input) {

		Map bashMap = [
			'[1;30m' : '<span style="color:black">',
			'[1;31m' : '<span style="color:red">',
			'[1;32m' : '<span style="color:green">',
			'[1;33m' : '<span style="color:yellow">',
			'[1;34m' : '<span style="color:blue">',
			'[1;35m' : '<span style="color:purple">',
			'[1;36m' : '<span style="color:cyan">',
			'[1;37m' : '<span style="color:white">',
			'[m'   : '</span>',
			'[0m'   : '</span>'
		]

		bashMap.each { k,v ->
			if (input.contains(k)) {
				//input = input.toString().replace(k, v)
				input = input.replace(k,'')
			}
		}
		return input
	}

	private void pingPong(Session usersession, Integer pingRate) {
		if (usersession && usersession.isOpen()) {
			boolean sendPong = false
			while ((sendPong==false)&&(usersession && usersession.isOpen())) {
					sleep(pingRate ?: 60000)
					usersession.basicRemote.sendText('ping')
			}
		}
	}

	
}
