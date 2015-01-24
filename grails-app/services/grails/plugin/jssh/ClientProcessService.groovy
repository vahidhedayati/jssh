package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient

/*
 * Vahid Hedayati Jan 2015
 *  This processingService class is bound to ClientListenerService which is 
 *  Bound to JsshClientEndPoint
 *  This class deals with processing information from front-end connection to backend and 
 *  processes backend results to frontend
 * 
 */
public class ClientProcessService extends ConfService  {


	def clientListenerService
	def messagingService
	def j2sshService
	def authService
	def dbStorageService
	private SshClient ssh = new SshClient()
	private SessionChannelClient session

	private SshConnectionProperties properties = null

	private String host, user, userpass, usercommand
	private int port = 22
	private boolean enablePong
	private int pingRate

	public void handleClose(Session userSession) {
		if (session && session.isOpen()) {
			session.close()
		}
		if (ssh && ssh.isConnected()) {
			ssh.disconnect()
		}
		userSession.close()
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
			String user = values.user as String
			String msg = values.msg as String
			
			// New Websocket Client connection register store 
			// each new command input from frontend
			String comloggerId = userSession.userProperties.get("comloggerId") as String
			String conloggerId = userSession.userProperties.get("conloggerId") as String
			dbStorageService.storeCommand(msg, user, conloggerId, username, comloggerId)
			 
			def asyncProcess = new Thread({
				SessionChannelClient ss = j2sshService.newShell( ssh,  session, userSession)
				j2sshService.processConnection(userSession, ss, msg)
				j2sshService.closeShell( ssh,  ss,  userSession)
			} as Runnable )
			asyncProcess.start()
			
		}else if (message.startsWith('/addGroup')) {
		
			def values = parseInput("/addGroup ",message)
			String user = values.user as String
			String msg = values.msg as String
			
			String output = dbStorageService.storeGroup(msg,user)
			messagingService.sendBackEndFM(username, output)
			
		}else if  (message.startsWith('DISCO:-')) {
		
			userSession.close()
			
		}else if (message.startsWith('{')) {
		
			JSONObject rmesg=JSON.parse(message)
			String actionthis=''
			String msgFrom = rmesg.msgFrom
			boolean pm = false

			if (rmesg.hostname) {
				verifyGeneric(rmesg)
				userSession.userProperties.put("pingRate", pingRate)
				boolean frontend = rmesg.frontend.toBoolean()
				if (frontend) {
					multiUser(ssh, session, properties, userSession)
				}
			}
			
			String disconnect = rmesg.system
			if (rmesg.privateMessage) {
				JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
				String command = rmesg2.command
			}
			
			if (disconnect && disconnect == "disconnect") {
				clientListenerService.disconnect(userSession)
			}
			
		}else{
			messagingService.sendBackEndFM(username, message)
		}
	}

	private void multiUser(SshClient ssh, SessionChannelClient session=null,
			SshConnectionProperties properties=null,Session userSession) {
			
		Map resSet = j2sshService.sshConnect(ssh, session, properties, user, userpass, host, 
			usercommand, port, userSession)

		boolean isAuthenticated = false
		SshClient ssh2
		SshConnectionProperties properties2
		SessionChannelClient session2
		if (resSet) {
			isAuthenticated = resSet.isAuthenticated
			ssh2  = resSet.ssh
			properties2 = resSet.properties
			session2 = resSet.session

		}
		//if (isAuthenticated) {}
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

		if (data.enablePong) {
			this.enablePong = data.enablePong?.toBoolean()
		}

		if (data.pingRate) {
			this.pingRate = data.pingRate?.toInteger() ?: '60000'
		}
	}
}

