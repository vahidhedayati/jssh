package grails.plugin.jssh

class ConnectSshTagLib {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	def grailsVersionService
	def grailsApplication
	
	def ajaxconnect =  { attrs, body ->
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand')?.toString()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('template')?.toString()
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
		def template=attrs.remove('template')?.toString()
		def port=attrs.remove('port')?.toString()
		def divId=attrs.remove('divId')?.toString()
		def wshostname=grailsApplication.config.jssh.wshostname ?: 'localhost:8080'
		
		if (template) {
			out << g.render(template:template, model: [divId:divId,wshostname:wshostname,hostname:hostname,port:port,password:password,username:username,userCommand:userCommand])
		}else{
			out << g.render(contextPath: pluginContextPath,template:"/connectSsh/socketprocess", model: [divId:divId,wshostname:wshostname,hostname:hostname,port:port,password:password,username:username,userCommand:userCommand])
		}
	}
	
	def chooseLayout =  { attrs, body ->
		def file = attrs.remove('file')?.toString()
		def loadtemplate=attrs.remove('loadtemplate')?.toString()
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand')?.toString()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('template')?.toString()
		def port=attrs.remove('port')?.toString()
		def input=attrs.remove('input')?.toString()
		def pageid=attrs.remove('pageid')?.toString() 
		def pagetitle=attrs.remove('pagetitle')?.toString()
		if (!pageid) { 
			pageid="jssh"
		}
		if (!pagetitle) {
			pagetitle="jssh J2SSH Websocket/Ajax plugin"
		}
		double verify=grailsVersionService.getGrailsVersion()
		
		def gfolder="resources"
		if (verify >= 2.4 ) {
			gfolder="assets"
		}
		out << g.render(contextPath: pluginContextPath, template: "/connectSsh/${gfolder}/${file}", model: [attrs:attrs,loadtemplate:loadtemplate,input:input,port:port,username:username,password:password,hostname:hostname,userCommand:userCommand,pageid:pageid,pagetitle:pagetitle])
	}
}
