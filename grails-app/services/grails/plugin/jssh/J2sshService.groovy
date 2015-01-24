package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.connection.ChannelOutputStream
import com.sshtools.j2ssh.connection.ChannelState
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile


class J2sshService extends ConfService {

	private boolean isAuthenticated = false
	private boolean pauseLog = false
	private boolean resumed = false
	
	def messagingService
	public processRequest(SshClient ssh, SessionChannelClient session,
		SshConnectionProperties properties,Session userSession, String message) {

	if  (message.equals('DISCO:-')) {
		session.close()
		ssh.disconnect()
	} else if  (message.equals('PAUSE:-')) {
		pauseLog = true
	} else if  (message.equals('RESUME:-')) {
		pauseLog = false
		resumed = true
	}else if (message.equals('CLOSE_SHELL:-')) {
		closeShell(ssh, session, userSession)
	}
}
	public void pingPong(Session userSession, Integer pingRate) {
		if (userSession && userSession.isOpen()) {
			String user = userSession.userProperties.get("username") as String
			boolean sendPong = false
			while ((sendPong==false)&&(userSession && userSession.isOpen())) {
				sleep(pingRate ?: 60000)
				messagingService.sendFrontEndPM(userSession, user,'ping')
			}
		}
	}

	private void closeShell(SshClient ssh, SessionChannelClient session) {
		int timeout = 1000;
		def cc = ssh.getActiveChannelCount() ?: 1
		if (cc>0) {
			session.close()
			session.getState().waitForState(ChannelState.CHANNEL_CLOSED, timeout);
		}
	}

	private SessionChannelClient newShell(SshClient ssh, SessionChannelClient session, Session userSession) {
		session = ssh.openSessionChannel()
		String user = userSession.userProperties.get("username") as String
		SessionOutputReader sor = new SessionOutputReader(session)
		if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
			if (session.startShell()) {
				ChannelOutputStream out = session.getOutputStream()

			}
		}

		def cc = ssh.getActiveChannelCount() ?: 1
		return session
	}

	private Map sshConnect(SshClient ssh, SessionChannelClient session,
			SshConnectionProperties properties,  String user, String userpass,
			String host, String usercommand, int port, Session userSession)  {

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

			def asyncProcess = new Thread({
				sleep(800)
				SessionChannelClient ss = newShell( ssh,  session, userSession)
				processConnection(userSession, ss, usercommand)
				closeShell( ssh,  ss)
			} as Runnable )
			asyncProcess.start()
			
		}else{
			def authType = "using key file  "
			if (password) { authType = "using password" }
			String failMessage = "SSH: Failed authentication user: ${username} on ${host} ${authType}"
			messagingService.sendFrontEndPM( userSession, user, failMessage)
		}
		return [isAuthenticated: isAuthenticated, ssh:ssh, session:session, properties: properties]
	}

	private void processConnection(Session userSession, SessionChannelClient session, String usercommand) {
		String user = userSession.userProperties.get("username") as String
		StringBuilder catchup = new StringBuilder()

		if (!usercommand.endsWith('\n')) {
			usercommand = usercommand+'\n'
		}

		session.getOutputStream().write(usercommand.getBytes())
		InputStream input = session.getInputStream()
		byte[] buffer = new byte[255]
		int read;
		int i=0
		if (userSession && userSession.isOpen()) {
			while ((read = input.read(buffer))>0) {
				String out1 = new String(buffer, 0, read)
				if (pauseLog) {
					catchup.append(out1)
				}else{
					if (resumed) {
						resumed = false
						messagingService.sendFrontEndPM2(userSession, user, catchup as String)
						catchup = new StringBuilder()
					}
					messagingService.sendFrontEndPM2(userSession, user, parseBash(out1))
				}
			}
		}
	}

}
