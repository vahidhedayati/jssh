package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class ClientProcessService extends ConfService  {


	def clientListenerService
	def autoCompleteService
	def messagingService

	public void processResponse(Session userSession, String message) {
		String username = userSession.userProperties.get("username") as String
		boolean disco = true
		if (message.startsWith("/pm")) {

			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			messagingService.forwardMessage(user,msg)

		}else if (message.startsWith('{')) {
			JSONObject rmesg=JSON.parse(message)
			String actionthis=''
			String msgFrom = rmesg.msgFrom
			boolean pm = false

			String disconnect = rmesg.system
			if (rmesg.privateMessage) {
				JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
				//checkMessage(userSession, username, rmesg2)
				String command = rmesg2.command
			}
			if (disconnect && disconnect == "disconnect") {
				clientListenerService.disconnect(userSession)
			}
			if (msgFrom ) {
				actionthis = rmesg.privateMessage
				pm = true
			}

			def rmessage = rmesg.message
			if (rmessage) {
				def matcher = (rmessage =~ /(.*): (.*)/)
				if (matcher.matches()){
					msgFrom = matcher[0][1]
					if (msgFrom) {
						actionthis = matcher[0][2]
					}
				}
			}

			if (actionthis) {
				if (actionthis == 'close_connection') {
					clientListenerService.disconnect(userSession)
				}else{
					if (!msgFrom.endsWith(username)) {
						clientListenerService.sendBackEndPM(userSession, username,actionthis)
					}
					if (disco) {
						clientListenerService.disconnect(userSession)
					}
				}
			}else{
				clientListenerService.sendFEPM(userSession, username, message)
			}
		}
	}


	private String parseFrontEnd(String username) {
		if (username.endsWith(frontend)) {
			username=username.substring(0, username.indexOf(frontend))
		}
		return username
	}

}

