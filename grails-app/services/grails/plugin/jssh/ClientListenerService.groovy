package grails.plugin.jssh


import java.util.Set;

import grails.converters.JSON

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ClientListenerService extends ConfService {


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

	def sendFEPM(Session userSession, String user,String message) {
		if (user && (!user.endsWith(frontend))) {
			user=user+frontend
		}
		if (user) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
	}

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
	Session p_connect(String _uri, String _username, String room, Map map){
		URI oUri
		if(_uri){
			oUri = URI.create(_uri+room);
		}
		def container = ContainerProvider.getWebSocketContainer()
		Session oSession
		try{
			oSession = container.connectToServer(JsshClientEndpoint.class, oUri)
			oSession.basicRemote.sendText((map as JSON).toString())
		}catch(Exception e){
			e.printStackTrace()
			if(oSession && oSession.isOpen()){
				oSession.close()
			}
			return null
		}
		oSession.userProperties.put("username", _username)
		return  oSession
	}


	public Session disconnect(Session _oSession){
		try{
			if(_oSession && _oSession.isOpen()){
				sendMessage(_oSession, DISCONNECTOR)
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return _oSession
	}

}
