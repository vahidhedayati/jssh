package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties

/*
 * Vahid Hedayati Jan 2015
 *  This processingService class is bound to ClientListenerService which is 
 *  Bound to JsshClientEndPoint
 *  This class deals with processing information from front-end connection to backend and 
 *  processes backend results to frontend
 * 
 */
public class JsshClientProcessService extends JsshConfService  {


	def jsshClientListenerService
	def messagingService
	def j2sshService
	def jsshAuthService
	def jsshDbStorageService

	private SshClient mySsh

	private SshConnectionProperties properties = null

	private String host, user, userpass, usercommand, sshKey, sshKeyPass
	private int port = 22
	private boolean enablePong
	private int pingRate

	public void handleClose(Session userSession) {
		if (mySsh && mySsh.isConnected()) {
			mySsh.disconnect()
		}
		if (userSession && userSession.isOpen()) {
			userSession.close()
		}
	}

	public void processResponse(Session userSession, String message) {

		String username = userSession.userProperties.get("username") as String
		boolean disco = true

		if (message.startsWith("/pm")) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			messagingService.forwardMessage(user,msg)

		}else if  (message.startsWith('/bm')) {
			def values = parseInput("/fm ",message)
			String cuser, chost
			String users = values.user as String
			if (users.indexOf('@')>-1) {
				cuser = users.substring(0,users.indexOf('@'))
				chost = users.substring(cuser.length()+1,users.length())
			}else{
				cuser = users
			}
			String msg = values.msg as String

			String comloggerId = userSession.userProperties.get("comloggerId") as String
			String conloggerId = userSession.userProperties.get("conloggerId") as String
			String realUser = userSession.userProperties.get("realUser") as String ?: username
			
			if (comloggerId && conloggerId) {
				jsshDbStorageService.storeCommand(msg, realUser, conloggerId, user, comloggerId)
			}

			def asyncProcess = new Thread({
				//SshClient mssh =  userSession.userProperties.get(chost) as SshClient
				//userSession.userProperties.put('host', chost)
				SshClient mssh =  userSession.userProperties.get('sshClient') as SshClient
				j2sshService.processConnection(mssh, userSession, msg)

			} as Runnable )
			asyncProcess.start()

		}else if (message.startsWith('/addGroup')) {

			def values = parseInput("/addGroup ",message)
			String user = values.user as String
			String msg = values.msg as String

			String output = jsshDbStorageService.storeGroup(msg,user)
			messagingService.sendBackEndFM(username, output)

		}else if  (message.startsWith('DISCO:-')) {
			//userSession.close()
			handleClose( userSession)
		}else if (message.startsWith('/system')) {
			def values = parseInput("/system ",message)
			String user = values.user as String
			String msg = values.msg as String
			parseJSON( userSession,  username, user, msg)
		}else if (message.startsWith('{')) {
			parseJSON( userSession, username, username, message)
		}else{
			messagingService.sendBackEndFM(username, message)
		}
	}

	private void parseJSON(Session userSession,String username, String user,  String message) {
		JSONObject rmesg=JSON.parse(message)
		String actionthis=''
		String msgFrom = rmesg.msgFrom
		boolean pm = false
		if (rmesg.hostname) {
			verifyGeneric(rmesg)
			userSession.userProperties.put("pingRate", pingRate)
			boolean frontend = rmesg.frontend.toBoolean()
			if (frontend) {
				multiUser(userSession)
			}
		}

		String system = rmesg.system
		if (rmesg.privateMessage) {
			JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
			String command = rmesg2.command
		}
		if (system) {
			if (user==username) {
				if (system == "PAUSE:-") {
					userSession.userProperties.put("status", "pause")
				}else if (system == "RESUME:-") {
					userSession.userProperties.put("status", "resume")
				}else if (system == "disconnect") {
					//clientListenerService.disconnect(userSession)
					handleClose( userSession)
				}
			}
		}
	}

	private void multiUser(Session userSession) {
		j2sshService.sshConnect(  user, userpass, host,	usercommand, port, sshKey, sshKeyPass, userSession)

	}

	private void verifyGeneric(JSONObject data) {
		this.host = data.hostname ?: ''
		//this.port

		if (data.port) {
			this.port = data.port.toInteger()
		}

		this.user = data.user ?: ''
		this.userpass = data.password ?: ''
		this.usercommand = data.usercommand ?: ''
		this.sshKey = data.sshKey ?: ''
		this.sshKeyPass = data.sshKey ?: ''
		if (data.enablePong) {
			this.enablePong = data.enablePong?.toBoolean()
		}

		if (data.pingRate) {
			this.pingRate = data.pingRate?.toInteger() ?: '60000'
		}
	}
}

