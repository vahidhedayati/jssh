package grails.plugin.jssh

import java.util.Set;

import grails.converters.JSON

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.session.SessionChannelClient;


class AuthService {
	def randService
	def jsshService
	private String host, user, userpass, usercommand
	private int port = 22
	private boolean enablePong
	private int pingRate

	public void authenticate(Set<Session> sshUsers, SshClient ssh, SessionChannelClient session,
			SshConnectionProperties properties,Session userSession, JSON data) {

		String jsshUser = data.jsshUser ?: randService.randomise('jsshUser')
		userSession.userProperties.put("username", jsshUser)
		boolean frontend = data.frontend.toBoolean()

		verifyGeneric(data)

		if (frontend) {
			multiUser(sshUsers, ssh, session, properties, userSession)
		}else{
			singleUser(ssh, session, properties, userSession)
		}

	}

	private void multiUser(Set<Session> sshUsers, SshClient ssh, SessionChannelClient session,
			SshConnectionProperties properties,Session userSession) {
			//TODO
			
	}		
				
	private void singleUser(SshClient ssh, SessionChannelClient session,
			SshConnectionProperties properties,Session userSession) {
		// Initial call lets connect
		def asyncProcess = new Thread({

			jsshService.sshConnect(ssh, session, properties, user, userpass, host, usercommand,
					port, userSession)} as Runnable )

		asyncProcess.start()

		def asyncProcess1 = new Thread({
			if (enablePong) {
				jsshService.pingPong(userSession, pingRate)
			}
		} as Runnable )
		asyncProcess1.start()
	}

	private void verifyGeneric(JSON data) {
		this.host = data.hostname ?: ''
		//this.port

		if (data.port) {
			this.port = data.port.toInteger()
		}
		this.user = data.user ?: ''
		this.userpass = data.password ?: ''
		this.usercommand = data.usercommand ?: ''

		if (data.enablePong) {
			this.enablePong = data.enablePong.toBoolean()
		}

		if (data.pingRate) {
			this.pingRate = data.pingRate.toInteger()
		}
	}
}
