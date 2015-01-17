package grails.plugin.jssh

import grails.converters.JSON

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient


class AuthService {

	def jsshService

	public void authenticate(SshClient ssh,Session userSession, JSON data) {
		String host = data.hostname ?: ''
		String port
		boolean enablePong
		int pingRate

		if (data.port) {
			port = data.port.toInteger()
		}
		
		String jsshUser = data.jsshUser ?: ''
		
		userSession.userProperties.put("username", jsshUser)
		
		boolean frontend = data.frontend.toBoolean()
		
		String user = data.user ?: ''
		String userpass = data.password ?: ''
		String usercommand = data.usercommand ?: ''

		if (data.enablePong) {
			enablePong = data.enablePong.toBoolean()
		}
		if (data.pingRate) {
			pingRate = data.pingRate.toInteger()
		}

		// Initial call lets connect
		def asyncProcess = new Thread({
			
			jsshService.sshConnect(ssh, user, userpass, host, usercommand,
				 port, userSession)} as Runnable )
		
		asyncProcess.start()
		
		sleep(1000)
		
		def asyncProcess1 = new Thread({
			if (enablePong) {
				jsshService.pingPong(userSession, pingRate)
			}
		} as Runnable )
		asyncProcess1.start()


	}

}
