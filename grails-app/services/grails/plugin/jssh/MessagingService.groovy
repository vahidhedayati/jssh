package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session


class MessagingService extends ConfService  {

	
	def sendFrontEndPM2(Session userSession, String user,String message) {
		user = addFrontEnd(user)
		def found = findUser( user)
		if (!found) {
			int i = 0
			while (i < 30 && (found==false)) {
				sleep(200)
				found = findUser( user)
				i++
			}
		}
		if (found) {
			def crec = usersSession(user)
			crec.basicRemote.sendText("${message}")
		}else{
			log.error "COULD NOT FIND ${user} : $message "
		}
	}

	def sendFrontEndPM(Session userSession, String user,String message) {
		user = addFrontEnd(user)
		def found = findUser( user)
		if (!found) {
			int i = 0
			while (i < 30 && (found==false)) {
				sleep(200)
				found = findUser( user)
				i++	
			}
		}
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}else{
			log.error "COULD NOT FIND ${user} : $message "
		}
	}

	def sendBackEndFM(String user,String message) {
		user = parseFrontEnd(user)
		String fend = addFrontEnd(user)
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
						//if (config.debug == "on") {
						//def cjob = crec.userProperties.get("job").toString()
						//println "sshUsers: ${cuser} ${cjob}"
						//}
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

	def fwdFendMsg(String username, String message) {
		username = parseFrontEnd(username)
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

	def sendBackPM(String user,String message, String mtype=null) {
		user = parseFrontEnd(user)
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						boolean found = false
						if (user == cuser) {


							if (mtype=="system") {
								crec.basicRemote.sendText("/system ${user},${message}")
							}else{
								crec.basicRemote.sendText("${message}")
							}
						}
					}
				}
			}

		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def sendJobPM(Session userSession, String job,String message) {
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						
						String cuser = crec.userProperties.get("username") as String
						String cjob =  crec.userProperties.get("job") as String
						//String chost =  crec.userProperties.get("host") as String
						
						boolean found = false
						
						if ((job == cjob) && (cuser && cuser.endsWith(frontend))){
						String fend = parseFrontEnd(cuser)
						if (fend) {
						println "-- $cuser ${fend}"
							Session buser = usersSession(fend)
							String host = buser.userProperties.get("host") as String
						//crec.basicRemote.sendText("/bm ${cuser}@${host},${message}")
						buser.basicRemote.sendText("/bm ${cuser}@${host},${message}")
						}
							//SshClient mssh =  crec.userProperties.get('sshClient') as SshClient
							//j2sshService.processConnection(mssh, crec, message)
						
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	
	/*
	 * try {
				synchronized (sshUsers) {
					sshUsers?.each { crec->
						if (crec && crec.isOpen()) {
							String cuser = crec.userProperties.get("username") as String
							
							boolean found = false
	
							//if ((job==cjob) && (cuser &&(!cuser.endsWith(frontend)))){
							if (job==cjob){
									Session fend = messagingService.usersSession(parseFrontEnd(cuser))
									if (fend) {
									SshClient mssh =  fend.userProperties.get('sshClient') as SshClient
									println "--- Messaging ${cuser} ${mssh} ${message}"
									j2sshService.processConnection(mssh, fend, message)
									}
								
							}
						}
					}
				}
			} catch (IOException e) {
				log.error ("onMessage failed", e)
			}
	 */
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
	 */

}
