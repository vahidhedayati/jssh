package grails.plugin.jssh


import grails.converters.JSON
import grails.util.Environment

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

import org.codehaus.groovy.grails.commons.GrailsApplication
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
	
	private GrailsApplication grailsApplication
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {
			
			// Adding this conflicts with listener added via plugin descriptor
			// Whilst it works as run-app - in production this causes issues
			def environment=Environment.current.name
			if (environment=='development') {
				serverContainer.addEndpoint(JsshEndpoint)
			}	

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)
			
			grailsApplication = ctx.grailsApplication
			def config = grailsApplication.config
			
			int defaultMaxSessionIdleTimeout = config.jssh.timeout ?: 0
			serverContainer.defaultMaxSessionIdleTimeout = defaultMaxSessionIdleTimeout
		}
		catch (IOException e) {
			log.error e.message, e
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	String host = ""
	String user = ""
	Integer port=22
	String userpass=""
	String usercommand = ""
	StringBuilder output=new StringBuilder()


	private SshClient ssh = new SshClient()
	private SessionChannelClient session
	private SshConnectionProperties properties = null
	private boolean isAuthenticated=false
	private boolean pauseLog=false
	private boolean resumed=false
	private boolean newSession=false
	private boolean sameSession=false
	//private InputStream input



	@OnOpen
	public String handleOpen(Session usersession) {
		usersession.basicRemote.sendText('Attempting SSH Connection')
		
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		
		grailsApplication= ctx.grailsApplication
	}


	@OnMessage
	public void handleMessage(String message, Session usersession) {
		def data=JSON.parse(message)
		// authentication stuff - system calls
		if (data) {
			host=data.hostname
			user=data.user
			userpass=data.password
			usercommand=data.usercommand
			if (data.port) {
				port=data.port
			}
			// Initial call lets connect
			def asyncProcess = new Thread({	sshConnect(user,userpass,host,usercommand,port as int,usersession)  } as Runnable )
			asyncProcess.start()

		} else{
			if  (message.equals('DISCO:-')) {
				session.close()
				ssh.disconnect()
			} else if  (message.equals('PAUSE:-')) {
				pauseLog=true
			} else if  (message.equals('RESUME:-')) {
				pauseLog=false
				resumed=true
			} else if  (message.equals('NEW_SESSION:-')) {
				newSession=true
				sameSession=false
			} else if  (message.equals('NEW_SHELL:-')) {
				newShell(usersession)
				sameSession=true
			}else if (message.equals('CLOSE_SHELL:-')) {
				closeShell(usersession)
				sameSession=true
			} else if  (message.equals('SAME_SESSION:-')) {
				sameSession=true
				newSession=false
			} else {
				//def config= Holders.config
				//def config = grailsApplication.config
				//def hideSendBlock=config.jssh.hideSendBlock
				// Will returm here conflicts with custom calls
				// Ensure user can actually send stuff according to backend config
				//if ((!hideSendBlock)||(!hideSendBlock.equals('YES'))) {
					def asyncProcess = new Thread({sshControl(message,usersession)  } as Runnable )
					asyncProcess.start()
				//}
			}

		}

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
		def myMsg=[:]
		int timeout = 1000;
		def cc=ssh.getActiveChannelCount() ?: 1
		if (cc>1) {
			session.close()
			session.getState().waitForState(ChannelState.CHANNEL_CLOSED,timeout);
			usersession.basicRemote.sendText('Shell closed')
		}else{
			usersession.basicRemote.sendText('Only 1 shell - could not close master window - try closing session : '+cc)
		} 
		def ncc=ssh.getActiveChannelCount() ?: 1
		myMsg.put("connCount", ncc.toString())
		def myMsgj=myMsg as JSON
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
		
		def cc=ssh.getActiveChannelCount() ?: 1
		def myMsg=[:]
		myMsg.put("connCount", cc.toString())
		def myMsgj=myMsg as JSON
		usersession.basicRemote.sendText(myMsgj as String)
		usersession.basicRemote.sendText('New shell created, console window : '+cc)
	}
	
	private void sshControl(String usercommand,Session usersession) {
		StringBuilder catchup=new StringBuilder()
		Boolean newChann=false
		//def config= Holders.config
		def config = grailsApplication.config
		String newchannel=config.jssh.NEWCONNPERTRANS
		String hideSessionCtrl=config.jssh.hideSessionCtrl

		if ((newchannel.equals('YES'))||((newSession)&&(sameSession==false))) { 
			newChann=true
		}else if (newSession) {
			newChann=true
		}else if (sameSession) {
			newChann=false
		}
		/*
		// Ensure user is not attempting to gain unauthorised access - check backend config ensure session control is enabled.
		if ((newchannel.equals('YES'))||(hideSessionCtrl.equals('NO')&&(newSession)&&(sameSession==false))) { 
			newChann=true
		}else if ((newSession)&&(hideSessionCtrl.equals('NO'))) {
			newChann=true
		}else if ((sameSession)&&(hideSessionCtrl.equals('NO'))) {
			newChann=false
		}
		*/

		def cc=ssh.getActiveChannelCount() ?: 1
		def myMsg=[:]
		myMsg.put("connCount", cc.toString())
		def myMsgj=myMsg as JSON
		usersession.basicRemote.sendText(myMsgj as String)
		if ((cc>1)&&(newChann==false)) {
			session.getOutputStream().write("${usercommand} \n".getBytes())
			InputStream input=session.getInputStream()
			byte[] buffer=new byte[255]
			int read;
			int i=0
			//def pattern = ~/^\s+$/
			while((read = input.read(buffer)) > 0)  {
				String out1 = new String(buffer, 0, read)
				//def m=pattern.matcher(out1).matches()
				//if (m==false) {
				if (pauseLog) {
					catchup.append(out1)
				}else{
					if (resumed) {
						resumed=false
						usersession.basicRemote.sendText(catchup as String)
						catchup=new StringBuilder()
					}
					usersession.basicRemote.sendText(out1)
				}
				//}
			}

		}else{
			session = ssh.openSessionChannel()
			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()
					session.getOutputStream().write("${usercommand} \n".getBytes())
					InputStream input=session.getInputStream()
					byte[] buffer=new byte[255]
					int read;
					int i=0
					//def pattern = ~/^\s+$/
					while((read = input.read(buffer)) > 0)  {
						String out1 = new String(buffer, 0, read)
						//def m=pattern.matcher(out1).matches()
						//if (m==false) {
						if (pauseLog) {
							catchup.append(out1)
						}else{
							if (resumed) {
								resumed=false
								usersession.basicRemote.sendText(catchup as String)
								catchup=new StringBuilder()
							}
							usersession.basicRemote.sendText(out1)
						}
						//}
					}
				}
			}
		}
		//session.close()
	}

	private void execCmd(String cmd,Session usersession) {
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

	private void sshConnect(String user,String userpass,String host,String usercommand, int port,Session usersession)  {
		//def config= Holders.config
		def config = grailsApplication.config
		String sshuser=config.jssh.USER
		String sshpass=config.jssh.PASS
		String sshkey=config.jssh.KEY
		String sshkeypass=config.jssh.KEYPASS
		String sshport=config.jssh.PORT

		String username = user ?: sshuser
		String password = userpass ?: sshpass
		int sshPort=port ?: sshport as Integer

		String keyfilePass=''
		int result=0

		properties = new SshConnectionProperties();
		properties.setHost(host)
		properties.setPort(sshPort)
		ssh.connect(properties, new IgnoreHostKeyVerification())
		
		println "---------- ${sshuser} ${sshkey.toString()} >${password}< |${sshpass}| -${userpass}-"
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
				isAuthenticated=true
			}
		}else{
			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient()
			pwd.setUsername(username)
			pwd.setPassword(password)
			result = ssh.authenticate(pwd)
			if(result == 4)  {
				isAuthenticated=true
			}
		}

		// Evaluate the result
		if (isAuthenticated) {
			sshControl(usercommand,usersession)
		}else{
			def authType="using key file  "
			if (password) { authType="using password" }
			usersession.basicRemote.sendText("SSH: Failed authentication user: ${username} on ${host} ${authType}")
		}
	}
}
