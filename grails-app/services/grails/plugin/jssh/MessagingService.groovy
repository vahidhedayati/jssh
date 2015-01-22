package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session


class MessagingService extends ConfService  {
	
	def sendFrontEndPM2(Session userSession, String user,String message) {
		if (user && (!user.endsWith(frontend))) {
			user=user+frontend
		}
		def found=findUser( user)
		if (found) {
			def crec = usersSession(user)
			crec.basicRemote.sendText("${message}")
		}else{
			log.error "COULD NOT FIND ${user} : $message "
		}
	}
	
	def sendFrontEndPM(Session userSession, String user,String message) {
		if (user && (!user.endsWith(frontend))) {
			user=user+frontend
		}
		def found=findUser( user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}else{
			log.error "COULD NOT FIND ${user} : $message "
		}
	}

	def sendBackEndFM(String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		String fend = user
		if (!user.endsWith(frontend)) {
			fend=user+frontend
		}
		Session cuser = usersSession(fend)
		String user1 = cuser.userProperties.get("username") as String
		cuser.basicRemote.sendText("/fm ${user},${message}")
	}

	boolean findUser(String username) {
		boolean found = false
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							found = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return found
	}


	public void sendMessage(Session userSession,final String message) {
		userSession.basicRemote.sendText(message)
	}
	
	def sendMsg(Session userSession,String msg) {
		try {
			userSession.basicRemote.sendText(msg)
		} catch (IOException e) {
		}
	}
	
	def messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}

	def forwardMessage(String username, String message) {
		Session user = usersSession(username)
		if (user) {
			user.basicRemote.sendText(message)
		}
	}
	
	def privateMessage(Session userSession,String user,String msg, String mtype) {
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.endsWith(frontend)) {
							if (mtype == "/fm") {
							}else{
							messageUser(crec,["message": "${msg}"])
							}
						}else{
							if (mtype == "/fm") {
							}else{
							crec.basicRemote.sendText("${mtype} ${urecord},"+msg as String)
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * To be used in later release  broadcast messages to master nodes of a given job
	 * A bit like terminator broadcasting to multiple ssh - to come
	def sendArrayPM(Session userSession, String job,String message) {
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (job==cjob) {
							found=findUser(cuser)
							if (found) {
								crec.basicRemote.sendText("/pm ${cuser},${message}")
							}
							if (!cuser.toString().endsWith(frontend)) {
								found=findUser(cuser+frontend)
								if (found) {
									crec.basicRemote.sendText("/pm ${cuser+frontend},${message}")
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def sendJobMessage(String job,String message) {
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (job==cjob) {
							crec.basicRemote.sendText("${message}")
						}
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}
	*/

	
	
	/*
	 * No longer required  required

	def sendBackPM(String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (user==cuser) {
							crec.basicRemote.sendText("${message}")
						}
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

  
	def sendBackEndPM(Session userSession, String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		userSession.basicRemote.sendText("/pm ${user},${message}")
	}
		
	def sendFEPM(Session userSession, String user,String message) {
		if (user && (!user.endsWith(frontend))) {
			user=user+frontend
		}
		if (user) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
	}
	 */

}
