package grails.plugin.jssh


import grails.converters.JSON
import grails.util.Holders

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.DeploymentException
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint

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

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServerContainer serverContainer = (ServerContainer) servletContextEvent.getServletContext()
				.getAttribute("javax.websocket.server.ServerContainer")

		try {
			serverContainer.addEndpoint(JsshEndpoint.class)
		} catch (DeploymentException e) {
			e.printStackTrace()
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
	//private InputStream input



	@OnOpen
	public String handleOpen(Session usersession) {
		usersession.getBasicRemote().sendText('Attempting SSH Connection')
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
				//session.close()
				ssh.disconnect()
			}   else{
				sshControl(message,usersession)
			}

		}
		
	}

	@OnClose
	public void handeClose() {
		//log.debug "Client is now disconnected."
		//session.close()
		ssh.disconnect()
	}
	
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
		//session.close()
		ssh.disconnect()
	}
	private void sshControl(String usercommand,Session usersession) {
		session = ssh.openSessionChannel()
		SessionOutputReader sor = new SessionOutputReader(session)
		if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
			if (session.startShell()) {
				ChannelOutputStream out = session.getOutputStream()
				session.getOutputStream().write("${usercommand} \n".getBytes())
				usersession.getBasicRemote().sendText("---------:"+usercommand+"")
				InputStream input=session.getInputStream()
				byte[] buffer=new byte[255]
				int read;
				int i=0
				//def pattern = ~/^\s+$/
				while((read = input.read(buffer)) > 0)  {
					String out1 = new String(buffer, 0, read)
					//def m=pattern.matcher(out1).matches()
					//if (m==false) {
						usersession.getBasicRemote().sendText(out1)
					//}
				}
			}
		}
		session.close()
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
				usersession.getBasicRemote().sendText(bos.toString())
			}
		}	  catch(Exception e)  {
			log.debug "Exception : " + e.getMessage()
		}
	}

	private void sshConnect(String user,String userpass,String host,String usercommand, int port,Session usersession)  {
		def config= Holders.config
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
			usersession.getBasicRemote().sendText("SSH: Failed authentication user: ${username} on ${host} ${authType}")
		}
	}
}