package grails.plugin.jssh.j2ssh

import grails.plugin.jssh.SshCommandBlackList
import grails.plugin.jssh.SshCommandRewrite
import grails.plugin.jssh.SshUser
import grails.transaction.Transactional

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.connection.ChannelOutputStream
import com.sshtools.j2ssh.connection.ChannelState
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader


class J2sshService extends JsshConfService {

	private boolean isAuthenticated = false

	def jsshMessagingService
	def j2sshConnectService
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
		Map jconn= j2sshConnectService.j2sshconnector(host,user,userpass,port)
		SshClient ssh = jconn.ssh
		isAuthenticated = jconn.isAuthenticated
		boolean password = jconn.password
		String username = jconn.username
		userSession.userProperties.put('sshClient', ssh)
		// Evaluate the result
		if (isAuthenticated) {
			userSession.userProperties.put('host', host)
			userSession.userProperties.put('connectedUsername', username)

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
		return isAuthenticated
	}

	private void closeConnection(SshClient mssh, String user) {
		disconnect(null, mssh, user)
	}

	private void processConnection(SshClient ssh, Session userSession, String usercommand) {
		String user = userSession.userProperties.get("username") as String
		String connectedUser = userSession.userProperties.get("connectedUsername") as String
		if (ssh && ssh.isConnected()) {
			SessionChannelClient session = new SessionChannelClient()
			StringBuilder catchup = new StringBuilder()
			int timeout = 1000
			session = ssh.openSessionChannel()

			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()

				}
			}

			boolean verifyNotBlocked = verifyBlackList(connectedUser, usercommand)


			if (verifyNotBlocked)  {

				usercommand = verifyRewrite(connectedUser, usercommand)

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
				jsshMessagingService.sendFrontEndPM2(userSession, user, "Command ${usercommand} appears to be in blackList")
			}
		}else{
			jsshMessagingService.sendFrontEndPM2(userSession, user, "no connection, ${usercommand} not executed")
		}
	}

	@Transactional
	private Boolean verifyBlackList(String connectedUser, String userCommand) {
		boolean goahead = true
		SshUser suser = SshUser.findByUsername(connectedUser)
		if (suser) {
			def blackList = SshCommandBlackList.findAllBySshuser(suser)
			blackList.each { bword ->
				if (userCommand.contains(bword.command)) {
					goahead = false
				}
			}
		}
		return goahead
	}

	@Transactional
	private String verifyRewrite(String connectedUser, String userCommand) {
		SshUser suser = SshUser.findByUsername(connectedUser)
		if (suser) {
			def rewrite = SshCommandRewrite.findAllBySshuser(suser)
			rewrite.each { bword ->
				if (userCommand.contains(bword.command)) {
					userCommand = userCommand.replace("${bword.command}", "${bword.replacement}")
				}

			}
		}
		return userCommand
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
