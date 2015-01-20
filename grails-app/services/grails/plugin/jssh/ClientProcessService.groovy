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

		}else if  (message.startsWith('/fm')) {
			def values = parseInput("/fm ",message)
			String user = values.user as String
			String msg = values.msg as String
			//println "we are in /FM "
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
		}else{

			clientListenerService.sendBackEndFM(username, message)
		}
	}

	private String parseFrontEnd(String username) {
		if (username.endsWith(frontend)) {
			username=username.substring(0, username.indexOf(frontend))
		}
		return username
	}

}

