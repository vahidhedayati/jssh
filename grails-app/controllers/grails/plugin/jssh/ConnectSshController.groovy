package grails.plugin.jssh

class ConnectSshController {

	def connectSsh
	def jsshConfig
	def grailsApplication
	def index() {}

	
	def socketprocess() {
		String hostname=params.hostname ?: 'localhost'
		String username=params.username
		String userCommand=params.command
		String password=params.password
		String template=params.template
		String port=params.port
		
		def wshostname=grailsApplication.config.jssh.wshostname ?: 'localhost:8080'
		if (template) {
			render (view: 'getTemplate', model: [username:username,port:port,password:password,hostname:hostname,userCommand:userCommand,template:template,wshostname:wshostname])
		}else{
			render (view: "/connectSsh/choose", model:[loadtemplate: 'socketprocess', username:username,port:port,password:password,hostname:hostname,userCommand:userCommand,wshostname:wshostname])
		}
	}
	
	

	def ajaxpoll() { 
		
	}
	
	
	def process() {
		String hostname=params.hostname ?: 'localhost'
		String username=params.username
		String userCommand=params.command
		String port=params.port
		if (username) {
			connectSsh.setUser(username)
		}
		String password=params.password
		if (password) {
			connectSsh.setUserpass(password)
		}
		String template=params.template
		connectSsh.setHost(hostname)
		connectSsh.setUsercommand(userCommand)
		session.referrer=request.getHeader('referer')
		def asyncProcess = new Thread({	connectSsh.ssh(jsshConfig)  } as Runnable )
		asyncProcess.start()
		def input=connectSsh.output
		if (template) {
			render (view: 'getTemplate', model: [input:input,port:port,hostname:hostname,userCommand:userCommand,template:template])
		}else{
			//render (template: 'process', model: [input:input,port:port,hostname:hostname,userCommand:userCommand])
			render (view: "/connectSsh/choose", model:[loadtemplate: 'process',input:input,username:username,port:port,password:password,hostname:hostname,userCommand:userCommand])
		}
	}


	def resetOutput() { 
		connectSsh.setOutput(new StringBuilder())
	}

	def closeConnection() {
		connectSsh?.closeConnection()
		log.info "Connection closed"
		redirect(url: session.referrer ?: '/')
	}

	def inspection() {
		def input=connectSsh.output
		render "${input}"
	}

	def inspect() {
		def input=connectSsh.output
		[input:input ]
	}
}
