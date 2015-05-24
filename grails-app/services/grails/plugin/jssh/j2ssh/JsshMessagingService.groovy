package grails.plugin.jssh.j2ssh

import grails.converters.JSON

import javax.websocket.Session


class JsshMessagingService extends JsshConfService  {

	static transactional  =  false

	def sendFrontEndPM2(Session userSession, String user,String message) {
		String urecord = userSession.userProperties.get("username") as String
		if (userSession && userSession.isOpen()) {
			user = addFrontEnd(user)
			boolean found = loopUser (user)
			if (found) {
				def crec = usersSession(user)
				if (crec && crec.isOpen()) {
					crec.basicRemote.sendText("${message}")
				}
			}else{
				log.error "COULD NOT FIND ${user} - Sending Disconnect"
				// 1.7 release - fixed ongoing messages being sent when frontend has left
				sendBackPM(urecord, "_DISCONNECT")

			}
		}
	}

	def sendFrontEndPM(Session userSession, String user,String message, String mtype=null) {
		String urecord = userSession.userProperties.get("username") as String
		if (userSession && userSession.isOpen()) {
			user = addFrontEnd(user)
			boolean found = loopUser (user)
			if (!mtype) {
				mtype="/pm"
			}
			if (found) {
				userSession.basicRemote.sendText("${mtype} ${user},${message}")
			}else{
				log.error "COULD NOT FIND ${user} - Sending Disconnect"
				// 1.7 release - fixed ongoing messages being sent when frontend has left
				sendBackPM(urecord, "_DISCONNECT")
			}
		}
	}

	def sendBackEndFM(String user,String message) {
		user = parseFrontEnd(user)
		String fend = addFrontEnd(user)
		Session crec = usersSession(fend)
		if (crec && crec.isOpen()) {
			String cuser = crec.userProperties.get("username") as String
			crec.basicRemote.sendText("/fm ${cuser},${message}")
		}
	}

	boolean findUser(String username) {
		boolean found = false
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (cuser.equals(username)) {
					found = true
				}
			}
		}

		return found
	}

	public void sendMessage(Session userSession,final String message) {
		if (userSession && userSession.isOpen()) {
			userSession.basicRemote.sendText(message)
		}
	}

	def sendMsg(Session userSession,String msg)throws IOException {
		try {
			if (userSession && userSession.isOpen()) {
				userSession.basicRemote.sendText(msg)
			}
		}catch (IOException e) {
			//throw new IOException("Could not message");
		}
	}

	def messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}

	def forwardMessage(String username, String message){
		Session user = usersSession(username)
		if (user && user.isOpen()) {
			user.basicRemote.sendText(message)
		}
	}

	def fwdFendMsg(String username, String message) {
		username = parseFrontEnd(username)
		Session user = usersSession(username)
		if (user && user.isOpen()) {
			user.basicRemote.sendText(message)
		}
	}

	def privateMessage(Session userSession,String user,String msg, String mtype) {
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
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

	def sendBackPM(String user,String message, String mtype=null) {
		user = parseFrontEnd(user)
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				//boolean found = false
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

	def sendJobPM(Session userSession, String job,String message) {
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				//boolean found = false
				if ((job == cjob) && (cuser && cuser.endsWith(frontend))){
					String fend = parseFrontEnd(cuser)
					if (fend) {
						Session buser = usersSession(fend)
						String host = buser.userProperties.get("host") as String
						buser.basicRemote.sendText("/bm ${cuser}@${host},${message}")
					}
				}
			}
		}

	}


	Session usersSession(String username) {
		Session userSession
		sshNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (cuser.equals(username)) {
					userSession=crec
				}
			}
		}
		return userSession
	}


	private boolean loopUser(String user) {
		boolean found = findUser(user)
		if (!found) {
			int i = 0
			while (i < 60 && (found==false)) {
				sleep(200)
				found = findUser( user)
				i++
			}
		}
		return found
	}


}