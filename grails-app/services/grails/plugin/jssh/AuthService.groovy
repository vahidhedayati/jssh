package grails.plugin.jssh

import java.util.Map;

import grails.converters.JSON

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient


class AuthService {

	def randService
	def jsshService
	def j2sshService

	private String host, user, userpass, usercommand
	private int port = 22
	private boolean enablePong
	private int pingRate

	public void authenticate(Set<Session> sshUsers, SshClient ssh, SessionChannelClient session=null,
			SshConnectionProperties properties=null,Session userSession, JSONObject data) {

		String jsshUser = data.jsshUser ?: randService.randomise('jsshUser')
		userSession.userProperties.put("username", jsshUser)
		boolean frontend = data.frontend.toBoolean()

		verifyGeneric(data)
		userSession.userProperties.put("pingRate", pingRate)
		if (frontend) {
			multiUser(sshUsers, ssh, session, properties, userSession)
		}else{
			if (user) {
				singleUser(ssh, session, properties, userSession)
			}
		}
	}

	private void multiUser(Set<Session> sshUsers, SshClient ssh, SessionChannelClient session=null, 
		SshConnectionProperties properties=null,Session userSession) {
		
		j2sshService.sshConnect(sshUsers, ssh, session, properties, user, userpass, host, usercommand, port, userSession)
	}

	private void singleUser(SshClient ssh, SessionChannelClient session=null,
			SshConnectionProperties properties=null,Session userSession) {
		// Initial call lets connect
		boolean go = true
		try {
			def asyncProcess = new Thread({ jsshService.sshConnect(ssh, session, properties, user, userpass, host, usercommand, port, userSession)
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
