package grails.plugin.jssh

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.connection.ChannelOutputStream
import com.sshtools.j2ssh.session.SessionChannelClient
import com.sshtools.j2ssh.session.SessionOutputReader
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile

class ConnectSsh {
	String host = ""
	String user = ""
	Integer port=22
	String userpass=""
	String usercommand = ""

	StringBuilder output = new StringBuilder()
	private SshClient ssh = new SshClient()
	SessionChannelClient session
	SshConnectionProperties properties = null
	private boolean isAuthenticated = false

	public String ssh(JsshConfig ac)  {
		try {
			processit(ac)
		} catch (InterruptedException ex) {
			output.append("Connection to host refused")
		}catch (IOException ex) {
			output.append("Connection to host refused IOException")
		}
		return
	}

	private void processit(JsshConfig ac)  throws IOException, InterruptedException {
		Object sshuser = ac.getConfig("USER")
		Object sshpass = ac.getConfig("PASS")
		Object sshkey = ac.getConfig("KEY")
		Object sshkeypass = ac.getConfig("KEYPASS")
		Object sshport = ac.getConfig("PORT")

		String username = user ?: sshuser.toString()
		String password = userpass ?: sshpass.toString()
		int sshPort = port ?: sshport as Integer
		String keyfilePass=''
		output = new StringBuilder()
		if (isAuthenticated) {
			session.close()
			ssh.disconnect()
		}

		properties = new SshConnectionProperties();
		properties.setHost(host)
		properties.setPort(sshPort)
		ssh.connect(properties, new IgnoreHostKeyVerification())
		int result=0
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
		}else{
			PasswordAuthenticationClient pwd = new PasswordAuthenticationClient()
			pwd.setUsername(username)
			pwd.setPassword(password)
			result = ssh.authenticate(pwd)
			if(result == 4)  {
				isAuthenticated = true
			}
		}
		// Evaluate the result
		if (isAuthenticated) {
			session = ssh.openSessionChannel()
			SessionOutputReader sor = new SessionOutputReader(session)
			if (session.requestPseudoTerminal("gogrid",80,24, 0 , 0, "")) {
				if (session.startShell()) {
					ChannelOutputStream out = session.getOutputStream()
					session.getOutputStream().write("${usercommand} \n".getBytes())
					InputStream input =session.getInputStream()
					byte[] buffer = new byte[255]
					int read;
					int i=0
					while((read = input.read(buffer)) > 0)  {
						String out1 = new String(buffer, 0, read)
						//out1=out1.replaceAll('<','&lt;')
						//out1=out1.replaceAll('>','&gt;')
						output.append(out1)
					}
					session.close()
					ssh.disconnect()
				}
			}
		}else{
			def authType="using key file ${sshkey} "
			if (password) { authType="using password" }
			output.append("Authentication has failed for user: ${username} on ${host} ${authType}")
		}
	}


	// TODO: part of next release - allow expect type of executions
	def qAndA(String command, String question, String reply, ChannelOutputStream out, SessionOutputReader sor) {
		out.write(command.getBytes())
		Thread.currentThread()
		Thread.sleep(1*30)
		String answer = null
		String aux = null
		aux = sor.getOutput()
		String[] str = aux.split("\n")
		answer = str[str.length-1]
		if(answer.contains(question))  {
			out.write("${reply} \n".getBytes())
		}
	}

	def closeConnection() {
		if (isAuthenticated) {
			session.close()
			ssh.disconnect()
		}

	}
}