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


/*
 *  Vahid Hedayati Jan 2015
 *  JsshService which deals with Websocket connections
 *  This is the traditional / older method which has no client / server modelling
 *  Data is instead passed through a load of async calls.
 */
class JsshService extends ConfService {

	private boolean isAuthenticated = false
	private boolean pauseLog = false
	private boolean resumed = false
	private boolean newSession = false
	private boolean sameSession = false

	public processRequest(SshClient ssh, SessionChannelClient session,
			SshConnectionProperties properties,Session userSession, String message) {

		if  (message.equals('DISCO:-')) {
			if (session && session.isOpen()) {
				session.close()
			}
			if (ssh && ssh.isConnected()) {
				ssh.disconnect()
			}
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
		}else if (message.equals('PING')) {
			pingPong(userSession)
		}else if (message.equals('PONG')) {

		} else {
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
		Boolean newChann = false
		String newchannel = config.NEWCONNPERTRANS ?: ''
		String hideSessionCtrl = config.hideSessionCtrl ?: ''

		if (config.security == "enabled") {
			if ((newchannel.equals('YES'))||(hideSessionCtrl.equals('NO')&&(newSession)&&(sameSession==false))) {
				newChann = true
			}else if ((newSession)&&(hideSessionCtrl.equals('NO'))) {
				newChann = true
			}else if ((sameSession)&&(hideSessionCtrl.equals('NO'))) {
				newChann = false
			}
		}
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
			processConnection(userSession, session,  usercommand)
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
	}

	public void pingPong(Session userSession) {
		int pingRate = userSession.userProperties.get("pingRate") as Integer
		if (userSession && userSession.isOpen()) {
			def asyncProcess = new Thread({
				if (config.debug == "on") {
					log.debug "Ping being sent from backend to front end "
				}
				sleep(pingRate ?: 60000)
				userSession.basicRemote.sendText('ping')
			} as Runnable )
			asyncProcess.start()
		}
	}

	private void closeShell(SshClient ssh, SessionChannelClient session, Session userSession) {
		def myMsg = [:]
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
		myMsg.put("connCount", ncc.toString())
		def myMsgj = myMsg as JSON
		userSession.basicRemote.sendText(myMsgj as String)
	}

	private void asyncProc(SshClient ssh,  SessionChannelClient session, Session userSession, String message) {
		def asyncProcess = new Thread({sshControl(ssh, session, message, userSession)} as Runnable )
		asyncProcess.start()
	}

	private void newShell(SshClient ssh, SessionChannelClient session, Session userSession) {
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
		userSession.basicRemote.sendText(myMsgj as String)
		userSession.basicRemote.sendText('New shell created, console window : '+cc)
	}

	private void sshConnect(SshClient ssh, SessionChannelClient session=null, SshConnectionProperties properties=null,  String user, String userpass,
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
			userSession.basicRemote.sendText(failMessage)
		}
	}

	private void processConnection(Session userSession, SessionChannelClient session, String usercommand) {
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
						catchup = new StringBuilder()
					}
					userSession.basicRemote.sendText(parseBash(out1))
				}
			}
		}
	}

	/*
	 private void execCmd(SshClient ssh, SessionChannelClient session, String cmd, Session userSession) {
	 try {
	 session = ssh.openSessionChannel();
	 if ( session.executeCommand(cmd) )	{
	 IOStreamConnector output = new IOStreamConnector();
	 java.io.ByteArrayOutputStream bos =  new
	 java.io.ByteArrayOutputStream();
	 output.connect(session.getInputStream(), bos );
	 session.getState().waitForState(ChannelState.CHANNEL_CLOSED);
	 userSession.basicRemote.sendText(bos.toString())
	 }
	 }	  catch(Exception e)  {
	 log.debug "Exception : " + e.getMessage()
	 }
	 }
	 */
}
