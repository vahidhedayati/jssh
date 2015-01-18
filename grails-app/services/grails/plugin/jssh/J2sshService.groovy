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
import com.sshtools.j2ssh.io.IOStreamConnector
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile


class J2sshService extends ConfService {

	private boolean isAuthenticated = false
	private boolean pauseLog = false
	private boolean resumed = false
	private boolean newSession = false
	private boolean sameSession = false

	def clientListenerService
	
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
		} else if  (message.equals('NEW_SESSION:-')) {
			newSession = true
			sameSession = false
		} else if  (message.equals('NEW_SHELL:-')) {
			newShell(ssh, session, userSession)
			sameSession = true
		}else if (message.equals('CLOSE_SHELL:-')) {
			closeShell(ssh, session, userSession)
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
					asyncProc(ssh, session, userSession, message)
				}
			}
			// No security just do it
			else{
				asyncProc(ssh, session, userSession, message)
			}
		}
	}

	public void sshControl(SshClient ssh, SessionChannelClient session, String usercommand, Session userSession) {
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
		userSession.basicRemote.sendText(myMsgj as String)
		if ((cc>1)&&(newChann==false)) {
			processConnection(userSession, session, usercommand)
		}else{
			session = ssh.openSessionChannel()
			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()
					processConnection(userSession, session, usercommand)
				}
			}
		}
		//session.close()
	}

	public void pingPong(Session userSession, Integer pingRate) {
		if (userSession && userSession.isOpen()) {
			String user = userSession.userProperties.get("username") as String
			boolean sendPong = false
			while ((sendPong==false)&&(userSession && userSession.isOpen())) {
				sleep(pingRate ?: 60000)
				//userSession.basicRemote.sendText('ping')
				clientListenerService.sendFrontEndPM(userSession, user,'ping')
			}
		}
	}


	private void closeShell(SshClient ssh, SessionChannelClient session, Session userSession) {
		//def myMsg = [:]
		String user = userSession.userProperties.get("username") as String
		int timeout = 1000;
		def cc = ssh.getActiveChannelCount() ?: 1
		if (cc>1) {
			session.close()
			session.getState().waitForState(ChannelState.CHANNEL_CLOSED, timeout);
			userSession.basicRemote.sendText('Shell closed')
		}else{
			userSession.basicRemote.sendText('Only 1 shell - could not close master window - try closing session : '+cc)
		}
		def ncc = ssh.getActiveChannelCount() ?: 1
		//myMsg.put("connCount", ncc.toString())
		def myMsgj = (["connCount": ncc.toString()]as JSON)
		//userSession.basicRemote.sendText(myMsgj as String)
		clientListenerService.sendFrontEndPM(userSession, user, myMsgj as String)
	}

	private void asyncProc(SshClient ssh,  SessionChannelClient session, Session userSession, String message) {
		def asyncProcess = new Thread({sshControl(ssh, session, message, userSession)} as Runnable )
		asyncProcess.start()
	}

	private void newShell(SshClient ssh, SessionChannelClient session, Session userSession) {
		session = ssh.openSessionChannel()
		String user = userSession.userProperties.get("username") as String
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
		//userSession.basicRemote.sendText(myMsgj as String)
		//userSession.basicRemote.sendText('New shell created, console window : '+cc)
		clientListenerService.sendFrontEndPM(userSession, user,myMsgj as String)
		clientListenerService.sendFrontEndPM(userSession, user, 'New shell created, console window : '+cc)
	}

	private void sshConnect(SshClient ssh, SessionChannelClient session,
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
			sshControl(ssh, session, usercommand, userSession)
		}else{
			def authType = "using key file  "
			if (password) { authType = "using password" }
			String failMessage = "SSH: Failed authentication user: ${username} on ${host} ${authType}"
			//userSession.basicRemote.sendText(failMessage)
			clientListenerService.sendFrontEndPM(userSession, user, failMessage)
		}
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
						userSession.basicRemote.sendText(parseBash(catchup as String))
						clientListenerService.sendFrontEndPM(userSession, user, catchup as String)
						catchup = new StringBuilder()
					}
					//userSession.basicRemote.sendText(parseBash(out1))
					clientListenerService.sendFrontEndPM(userSession, user, catchup as String)
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

}
