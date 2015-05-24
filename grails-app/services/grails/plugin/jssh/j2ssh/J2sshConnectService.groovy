package grails.plugin.jssh.j2ssh

import javax.websocket.Session

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile


class J2sshConnectService extends JsshConfService {

	public Map j2sshconnector(String host,String user, String userpass, int port) {
		String sshuser = config.USER ?: ''
		String sshpass = config.PASS ?: ''
		String sshkey = config.KEY ?: ''
		String sshkeypass = config.KEYPASS ?: ''
		String sshport = config.PORT ?: ''

		String kex = config.kex ?: ''
		String macs = config.macs ?: ''

		String username = user ?: sshuser
		String password = userpass ?: sshpass
		int sshPort = port ?: sshport as Integer
		boolean isAuthenticated = false
		boolean isPass = false

		String keyfilePass=''
		int result=0
		SshConnectionProperties properties = new SshConnectionProperties()

		if (kex) {
			properties.setPrefKex(kex)
		}
		if (macs) {
			properties.setPrefSCMac(macs)
		}

		boolean go = true
		SshClient ssh
		try {
			properties.setHost(host)
			properties.setPort(sshPort)
			ssh = new SshClient()
			ssh.connect(properties, new IgnoreHostKeyVerification())
		} catch (Exception e) {
			log.debug "${e}"
			go = false
		}
		if (go && ssh) {
			// User has a key authenticate using SSH Key
			if (!password) {
				PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient()
				pk.setUsername(username)

				SshPrivateKeyFile file = SshPrivateKeyFile.parse(new File(sshkey.toString()))
				if (file.isPassphraseProtected()) {
					keyfilePass = sshkeypass.toString()
				}
				SshPrivateKey key = file.toPrivateKey(keyfilePass);
				pk.setKey(key)
				// Try the authentication
				result = ssh.authenticate(pk)
				if (result == AuthenticationProtocolState.COMPLETE) {
					isAuthenticated = true
				}
			}
			// A password has been provided - attempt to ssh using password
			else{
				isPass = true
				PasswordAuthenticationClient pwd = new PasswordAuthenticationClient()
				pwd.setUsername(username)
				pwd.setPassword(password)
				result = ssh.authenticate(pwd)
				if(result == 4)  {
					isAuthenticated = true
				}
			}
		}
		return [ssh:ssh,isAuthenticated:isAuthenticated, password:isPass, username:username]
	}
}
