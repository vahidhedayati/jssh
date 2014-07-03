package grails.plugin.jssh

class ConnectSshController {

	def connectSsh
	def jsshConfig

	def index() {}

	def process() {
		String hostname=params.hostname ?: 'localhost'
		String username=params.username
		String userCommand=params.command
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
			render (view: 'getTemplate', model: [input:input,hostname:hostname,userCommand:userCommand,template:template])
		}else{
			render (template: 'process', model: [input:input,hostname:hostname,userCommand:userCommand])
		}
	}

	def increaseBuffer(String nsize) { 
		connectSsh.setBz(nsize)
		render nsize
	}
	def closeConnection() {
		connectSsh?.closeConnection()
		log.info "Connection closed"
		redirect(url: session.referrer ?: '/')
	}

	def inspection() {
		def input=connectSsh.output
		render "${input ?: 'Please be patient - console output will appear shortly'}"
	}

	def inspect() {
		def input=connectSsh.output
		[input:input ]
	}
}
