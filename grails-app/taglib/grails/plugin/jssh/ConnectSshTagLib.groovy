package grails.plugin.jssh

class ConnectSshTagLib {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	def grailsApplication
	
	def ajaxconnect =  { attrs, body ->
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand')?.toString()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('password')?.toString()
		def port=attrs.remove('port')?.toString()
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
			out << g.render(template:template, model: [input:input,port:port,hostname:hostname,userCommand:userCommand])
		}else{
			out << g.render(contextPath: pluginContextPath,template:'/connectSsh/process', model: [input:input,port:port,hostname:hostname,userCommand:userCommand])
		}
	}
	
	def socketconnect =  { attrs, body ->
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand')?.toString()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('password')?.toString()
		def port=attrs.remove('port')?.toString()
		if (template) {
			out << g.render(template:template, model: [hostname:hostname,port:port,password:password,username:username,userCommand:userCommand])
		}else{
			out << g.render(contextPath: pluginContextPath,template:'/connectSsh/socketprocess', model: [hostname:hostname,port:port,password:password,username:username,userCommand:userCommand])
		}
	}
	
	def chooseLayout =  { attrs, body ->
		def file = attrs.remove('file')?.toString()
		
		def gver=grailsApplication.metadata['app.grails.version']
		double verify=getGrailsVersion(gver)
		if (verify >= 2.4 ) {
			out << g.render(contextPath: pluginContextPath, template: "/connectSsh/assets/${file}", model: [attrs:attrs])
		}else{
			out << g.render(contextPath: pluginContextPath, template: "/connectSsh/resources/${file}", model: [attrs:attrs])
		}
	}
	
	private getGrailsVersion(String appVersion) {
		if (appVersion && appVersion.indexOf('.')>-1) {
			int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
			double verify=appVersion.substring(0,lastPos) as double
		}
	}
}
