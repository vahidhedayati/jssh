package grails.plugin.jssh.j2ssh

import grails.converters.JSON

import javax.websocket.Session

import org.grails.web.json.JSONObject

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

	static transactional  =  false

	def jsshClientListenerService
	def jsshMessagingService
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
		String uusername = userSession.userProperties.get("username") as String
		String ujob = userSession.userProperties.get("job") as String
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				if (ujob == cjob) {
					if (crec && crec.isOpen()) {
						if (!cuser.endsWith(frontend)) {
							jsshMessagingService.sendMsg(crec, "_DISCONNECT")
						}else{
							if (debug) {
								log.info "Closing Websocket for ${cuser}"
							}
							destroySshUser(cuser)
							/*
							 if (crec && crec.isOpen()) {
							 crec.close()
							 sleep(2)
							 }
							 */
						}
					}
				}
			}
		}

	}

	public void processResponse(Session userSession, String message) {

		String username = userSession.userProperties.get("username") as String
		boolean disco = true

		if (message.startsWith("/pm")) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			jsshMessagingService.forwardMessage(user,msg)
		}else if (message.startsWith("_DISCONNECT")) {
			SshClient amssh  =  userSession.userProperties.get('sshClient') as SshClient
			j2sshService.closeConnection(amssh, username)
			//userSession.close()
			destroySshUser(username)
			sleep(100)
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
			String host = userSession.userProperties.get("host") as String

			//String hosts = userSession.userProperties.get("hosts") as Map
			// 	TODO - null being returned for host in broadcast
			// Not a lot can be done - this is due to multiple broadcasting.
			// should then be traceable via connection history and time of command execution

			if (conloggerId) {
				jsshDbStorageService.storeCommand(msg, chost ?: host, realUser, conloggerId, user, comloggerId)
			}

			try {
				def asyncProcess = new Thread({
					SshClient mssh  =  userSession.userProperties.get('sshClient') as SshClient
					j2sshService.processConnection(mssh, userSession, msg)
				} as Runnable )
				asyncProcess.start()
			} catch (Exception e) {
				e.printStackTrace()
			}


		}else if (message.startsWith('/system')) {
			def values = parseInput("/system ",message)
			String user = values.user as String
			String msg = values.msg as String
			parseJSON( userSession,  username, user, msg)
		}else if (message.startsWith('{')) {
			parseJSON( userSession, username, username, message)
		}else{
			jsshMessagingService.sendBackEndFM(username, message)
		}
	}

	private void parseJSON(Session userSession,String username, String user,  String message) {
		JSONObject rmesg=JSON.parse(message)
		String actionthis=''
		String msgFrom = rmesg.msgFrom
		boolean pm = false
		boolean go = false
		String enablePong1 = userSession.userProperties.get("enablePong") ?: config.enablePong ?: 'false'
		def pingRate1 = userSession.userProperties.get("pingRate") as Integer ?: config.pingRate ?: 60000

		if (rmesg.hostname) {
			verifyGeneric(rmesg)
			///sshUsers.putIfAbsent(username, userSession)
			boolean frontend = rmesg.frontend.toBoolean()
			boolean go2 = isConfigEnabled(enablePong1)
			if (frontend) {
				go = multiUser(userSession, go2, pingRate1 as Integer)
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
				}else if (system.equals('PONG')) {
					if (enablePong1) {
						j2sshService.pingPong(userSession, pingRate1, username)
					}
				}
			}
		}
	}

	private Boolean multiUser(Session userSession, boolean enablePong1, int pingRate1) {
		return j2sshService.sshConnect( enablePong1, pingRate1, user, userpass, host,	usercommand, port, sshKey, sshKeyPass, userSession)
	}

	private void verifyGeneric(JSONObject data) {
		this.host = data.hostname ?: ''

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

