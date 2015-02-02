package grails.plugin.jssh

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


class J2sshService extends JsshConfService {

	private boolean isAuthenticated = false

	def jsshMessagingService

	public void pingPong(Session userSession, Integer pingRate, String username) {
		if (userSession && userSession.isOpen()) {
			String user = userSession.userProperties.get("username") as String
			def asyncProcess = new Thread({
				sleep(pingRate ?: 60000)
				if (userSession && userSession.isOpen()) {
					jsshMessagingService.sendFrontEndPM(userSession,user,'ping','/fm')
				}
			} as Runnable )
			asyncProcess.start()
		}
	}

	private Boolean sshConnect(Boolean enablePong, Integer pingRate, String user, String userpass, String host, String usercommand, int port,
			String sshKey, String sshKeyPass, Session userSession)  {
		String suser = userSession.userProperties.get("username") as String
		String sshuser = config.USER ?: ''
		String sshpass = config.PASS ?: ''
		//String sshkey = config.KEY ?: ''
		//String sshkeypass = config.KEYPASS ?: ''
		String sshport = config.PORT ?: ''

		String username = user ?: sshuser
		String password = userpass ?: sshpass
		int sshPort = port ?: sshport as Integer
		String keyfilePass=''
		int result=0

		SshConnectionProperties properties = new SshConnectionProperties()
		boolean go = true
		SshClient ssh
		try {
			properties.setHost(host)
			properties.setPort(sshPort)
			ssh = new SshClient()
			ssh.connect(properties, new IgnoreHostKeyVerification())
		} catch (Exception e) {
			go = false
			//jsshMessagingService.sendFrontEndPM(userSession, user,"ssh connection to ${host} refused")
		}
		if (go && ssh) {

			// User has a key authenticate using SSH Key
			if (!password) {
				PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient()
				pk.setUsername(username)

				SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(sshKey ?: ''))
				if (file.isPassphraseProtected()) {
					keyfilePass = sshKeyPass ?: ''
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

			userSession.userProperties.put('sshClient', ssh)

			// Evaluate the result
			if (isAuthenticated) {
				userSession.userProperties.put('host', host)
				try {
					def asyncProcess = new Thread({
						processConnection(ssh, userSession, usercommand)
					} as Runnable )
					asyncProcess.start()
				} catch (Exception e) {
					e.printStackTrace()
				}
				if (enablePong) {
					pingPong(userSession, pingRate, username)
				}
			}else{
				def authType = "using key file  "
				if (password) { authType = "using password" }
				String failMessage = "SSH: Failed authentication user: ${username} on ${host} ${authType}"
				jsshMessagingService.sendFrontEndPM( userSession, user, failMessage)
			}
		}

		return isAuthenticated
	}

	private void closeConnection(SshClient mssh, String user) {
		disconnect( null, mssh, user )
	}

	private void processConnection(SshClient ssh, Session userSession, String usercommand) {
		String suser = userSession.userProperties.get("username") as String
		if (ssh && ssh.isConnected()) {
			SessionChannelClient session = new SessionChannelClient()
			StringBuilder catchup = new StringBuilder()
			int timeout = 1000
			session = ssh.openSessionChannel()
			String user = userSession.userProperties.get("username") as String
			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()

				}
			}
			if (!usercommand.endsWith('\n')) {
				usercommand = usercommand+'\n'
			}

			session.getOutputStream().write(usercommand.getBytes())
			InputStream input = session.getInputStream()
			byte[] buffer = new byte[255]
			int read;
			int i=0
			boolean go = true
			if (userSession && userSession.isOpen()) {
				while (((read = input.read(buffer))>0)&&(go)) {

					String status = userSession.userProperties.get("status") as String
					String out1 = new String(buffer, 0, read)
					if (status == "pause") {
						catchup.append(out1)
					}else if (status == "disconnect") {
						disconnect( session, ssh, user )
					}else{
						if (catchup) {
							jsshMessagingService.sendFrontEndPM2(userSession, user, catchup as String)
							catchup = new StringBuilder()
						}
						jsshMessagingService.sendFrontEndPM2(userSession, user, parseBash(out1))
					}

				}

				def cc = ssh.getActiveChannelCount() ?: 1
				if (cc>0) {
					session.close()
					session.getState().waitForState(ChannelState.CHANNEL_CLOSED, timeout);
				}
			}else{
				disconnect( session, ssh, user )
			}
		}else{
			jsshMessagingService.sendFrontEndPM2(userSession, suser, "no connection, ${usercommand} not executed")
		}
	}

	private void disconnect(SessionChannelClient session=null,SshClient ssh,String user ) {
		if (debug) {
			log.info"ssh session being closed for ${user}"
		}
		if (session) {
			session.close()
		}
		ssh.disconnect()
	}

}
