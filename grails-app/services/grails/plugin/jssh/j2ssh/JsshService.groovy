package grails.plugin.jssh.j2ssh

import grails.converters.JSON

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.connection.ChannelOutputStream
import com.sshtools.j2ssh.connection.ChannelState
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader


/*
 *  Vahid Hedayati Jan 2015
 *  Revist March 2015 - due to bugs left in Jan
 *  JsshService which deals with Websocket connections <jssh:socketconnect taglib
 *  
 *  This is the traditional / older method which has no client / server modelling
 *  Data is instead passed through a load of async calls.
 *  
 */
class JsshService extends JsshConfService {

	static transactional  =  false

	private boolean isAuthenticated = false
	private boolean pauseLog = false
	private boolean resumed = false
	private boolean newSession = false
	private boolean sameSession = false

	def j2sshConnectService

	public processRequest(Session userSession, String message) {

		SshClient ssh  =  userSession.userProperties.get("sshClient") as SshClient
		SessionChannelClient session = userSession.userProperties.get("sshSession") as SessionChannelClient

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

	public void sshControl(SshClient ssh,  SessionChannelClient session, String usercommand, Session userSession) {
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
		try {
			if ((cc>1)&&(newChann==false)) {
				processConnection(userSession, session,  usercommand)
			}else{
				session = ssh.openSessionChannel()
				userSession.userProperties.put('sshSession', session)
				SessionOutputReader sor = new SessionOutputReader(session)
				if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
					if (session.startShell()) {
						ChannelOutputStream out = session.getOutputStream()
						processConnection(userSession, session, usercommand)
					}
				}
			}
		}catch (Exception e) {
		}
	}

	public void pingPong(Session userSession) {
		int pingRate = userSession.userProperties.get("pingRate") as Integer
		if (userSession && userSession.isOpen()) {
			try {
				def asyncProcess = new Thread({
					if (debug) {
						log.debug "Ping being sent from backend to front end "
					}
					sleep(pingRate ?: 60000)
					verifySession(userSession,'ping')
				} as Runnable )
				asyncProcess.start()
			} catch (Exception e) {
				//	e.printStackTrace()
			}
		}
	}

	private void closeShell(SshClient ssh, SessionChannelClient session, Session userSession) {
		int timeout = 1000;
		def cc = ssh.getActiveChannelCount() ?: 1
		if (cc>1) {
			session.close()
			session.getState().waitForState(ChannelState.CHANNEL_CLOSED, timeout);
			verifySession(userSession,'Shell closed')
		}else{
			verifySession(userSession,'Only 1 shell - could not close master window - try closing session : '+cc)
		}
		def ncc = ssh.getActiveChannelCount() ?: 1
		Map myMsg = ["connCount" : ncc.toString()]
		verifySession(userSession,(myMsg as JSON).toString())
	}

	private void asyncProc(SshClient ssh,  SessionChannelClient session, Session userSession, String message) {
		//ssh, session,
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
		Map myMsg = ["connCount" : cc.toString()]
		verifySession(userSession,(myMsg as JSON).toString())
		verifySession(userSession, 'New shell created, console window : '+cc)
	}

	private Boolean sshConnect(String user, String userpass, String host, String usercommand, int port, Session userSession)  {
		Map jconn= j2sshConnectService.j2sshconnector(host,user,userpass,port)
		SshClient ssh = jconn.ssh
		isAuthenticated = jconn.isAuthenticated
		boolean password = jconn.password
		String username = jconn.username
		// Evaluate the result
		if (isAuthenticated) {
			sleep(200)
			if (userSession && userSession.isOpen()) {
				userSession.userProperties.put('sshClient', ssh)
				SessionChannelClient session = new SessionChannelClient()
				//userSession.userProperties.put('sshSession', session)
				sshControl(ssh, session, usercommand, userSession)
			}
		}else{
			def authType = "using key file  "
			if (password) { authType = "using password" }
			String failMessage = "SSH: Failed authentication user: ${username} on ${host} ${authType}"
			verifySession(userSession, failMessage)
		}


		return isAuthenticated
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
						verifySession(userSession, parseBash(catchup as String))
						catchup = new StringBuilder()
					}
					verifySession(userSession, parseBash(out1))
				}
			}
		}
	}

	private void verifySession(Session userSession, String command) {
		if (userSession && userSession.isOpen()) {
			userSession.basicRemote.sendText(command)
		}
	}

}
