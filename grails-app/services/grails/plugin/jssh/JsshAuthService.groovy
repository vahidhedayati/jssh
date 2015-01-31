package grails.plugin.jssh

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient

/*
 * Vahid Hedayati 
 * AuthService was originally set up as a way of determining what type of socket connection
 *  it was and then defining which jssh service to connect to 
 *  client/server changed to push info back to processService to do connection
 *  have a look for multiUser in processService which used to be all in here.
 *   
 */
class JsshAuthService extends JsshConfService {

	def jsshRandService
	def jsshService
	def j2sshService

	private String host, user, userpass, usercommand
	private int port = 22
	private boolean enablePong
	private int pingRate

	// Shared handleClose for jssh:conn call method 
	// moved here so that jsshMessaging service can use it
	public void handleClose(Session userSession) {
		String uusername = userSession.userProperties.get("username") as String
		String ujob = userSession.userProperties.get("job") as String
		try {
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						String cjob =  crec.userProperties.get("job") as String
						String cuser = crec.userProperties.get("username") as String
						if (ujob == cjob) {

							if (!cuser.endsWith(frontend)) {
								jsshMessagingService.sendMsg(crec, "_DISCONNECT")
							}else{
								if (config.debug == "on") {
									log.info "Closing Websocket for ${cuser}"
								}
								crec.close()
							}
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("handleClose failed", e)
		}

	}
	
	public void authenticate(SshClient ssh, SessionChannelClient session=null,
			SshConnectionProperties properties=null,Session userSession, JSONObject data) {

		String jsshUser = data.jsshUser ?: jsshRandService.randomise('jsshUser')
		userSession.userProperties.put("username", jsshUser)
		verifyGeneric(data)
		userSession.userProperties.put("pingRate", pingRate)
		if (user) {
			singleUser(ssh, session, properties, userSession)
		}
	}


	private void singleUser(SshClient ssh, SessionChannelClient session=null,
			SshConnectionProperties properties=null,Session userSession) {
		// Initial call lets connect
		boolean go = true
		try {
			def asyncProcess = new Thread({ 
				sleep(300)
				jsshService.sshConnect(ssh, session, properties, user, userpass, host, usercommand, port, userSession)
			} as Runnable )
			asyncProcess.start()

		} catch (Exception e) {
			go = false
			//	e.printStackTrace()
		}

		if (enablePong && go ) {
			jsshService.pingPong(userSession)
		}
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
