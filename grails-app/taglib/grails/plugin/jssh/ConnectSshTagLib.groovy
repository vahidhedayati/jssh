package grails.plugin.jssh

class ConnectSshTagLib {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	
	def connect =  { attrs, body ->
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand')?.toString()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('password')?.toString()
		if (!userCommand) {
			throwTagError("Tag [connect] is missing required attribute [userCommand]")
		}
		if (!hostname) {
			throwTagError("Tag [connect] is missing required attribute [hostname]")
		}
		
		if (username) {
			connectSsh.setUser(username)
		}
		
		if (password) {
			connectSsh.setUserpass(password)
		}
		
		connectSsh.setHost(hostname)
		connectSsh.setUsercommand(userCommand)
		session.referrer=request.getHeader('referer')
		def asyncProcess = new Thread({	connectSsh.ssh(jsshConfig)  } as Runnable )
		asyncProcess.start()
		def input=connectSsh.output
		
		if (template) { 
			out << g.render(template:template, model: [input:input,hostname:hostname,userCommand:userCommand])
		}else{
			out << g.render(contextPath: pluginContextPath,template:'/connectSsh/process', model: [input:input,hostname:hostname,userCommand:userCommand])
		}
	}
}
