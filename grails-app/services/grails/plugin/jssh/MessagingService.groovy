package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session


class MessagingService extends ConfService  {

	def sendMsg(Session userSession,String msg) {
		try {
			userSession.basicRemote.sendText(msg)
		} catch (IOException e) {
		}
	}

	def forwardMessage(String username, String message) {
		Session user = usersSession(username)
		if (user) {
			println "== found ${username} sending $message"
			user.basicRemote.sendText(message)
		}
	}

	def privateMessage(Set<Session> sshUsers, Session userSession,String user,String msg) {
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							found = true
							if (cuser.endsWith(frontend)) {
								messageUser(crec,["message": "${msg}"])
							}else{
								crec.basicRemote.sendText(msg as String)
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}


	Session usersSession(String username) {
		Session userSession
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							userSession=crec
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return userSession
	}

	def messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}


}
