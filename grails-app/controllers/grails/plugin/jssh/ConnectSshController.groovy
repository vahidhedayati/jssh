package grails.plugin.jssh

class ConnectSshController {
	def connectSsh
	def jsshConfig
	//def grailsApplication
//(.*[A-z])=([A-z].*)	
//$1 = $2
			
//([A-Za-z])\,([A-Za-z])
//	$1\, $2
	def index() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock = config.hideAuthBlock 
		[hideAuthBlock:hideAuthBlock]
	}
	
	
	def socketremote() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
	}

	def ajaxpoll() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
	}
	
	def socketprocess() {
		String hostname = params.hostname ?: 'localhost'
		String username = params.username
		String userCommand = params.command
		String password = params.password
		String template = params.template
		String port = params.port
		String divId = params.divId ?: 'Basic'
		String remoteForm = params.remoteForm
		def wshostname = config.wshostname ?: 'localhost:8080'
		def hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		def hideSendBlock = config.hideSendBlock ?: 'YES'
		def hideSessionCtrl = config.hideSessionCtrl ?: 'YES'
		def hideWhatsRunning = config.hideWhatsRunning ?: 'NO'
		def hideDiscoButton = config.hideDiscoButton ?: 'NO'
		def hidePauseControl = config.hidePauseControl ?: 'NO'
		def hideNewShellButton = config.hideNewShellButton ?: 'YES'
		
		Map model= [hideNewShellButton:hideNewShellButton, hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, 
			hidePauseControl:hidePauseControl, hideSessionCtrl:hideSessionCtrl, hideConsoleMenu:hideConsoleMenu, hideSendBlock:hideSendBlock,
			divId:divId, username:username, port:port, password:password, hostname:hostname, userCommand:userCommand, wshostname:wshostname]
		
		if (template) {
			model.put(template:template)
			render (view: 'getTemplate', model: model )
		}else{
			if ( (remoteForm) && (remoteForm.equals('true'))) { 
				render (template: "/connectSsh/socketprocess", model:model)
			}else{
				model.put(loadtemplate: 'socketprocess')
				render (view: "/connectSsh/choose", model:model)
			}
		}
	}
	
	def process() {
		String hostname = params.hostname ?: 'localhost'
		String username = params.username
		String userCommand = params.command
		String port = params.port
		if (username) {
			connectSsh.setUser(username)
		}
		String password = params.password
		if (password) {
			connectSsh.setUserpass(password)
		}
		String template = params.template
		connectSsh.setHost(hostname)
		connectSsh.setUsercommand(userCommand)
		session.referrer = request.getHeader('referer')
		
		def asyncProcess = new Thread({	connectSsh.ssh(jsshConfig)  } as Runnable )
		asyncProcess.start()
		def input = connectSsh.output
		
		Map model = [input:input, port:port, hostname:hostname, userCommand:userCommand ]
		
		
		if (template) {
			model.put(template:template)
			render (view: 'getTemplate', model: model)
		}else{
			model.put(loadtemplate: 'process')
			render (view: "/connectSsh/choose", model: model)
		}
	}

	private getConfig() { 
		grailsApplication.config?.jssh
	}
	def resetOutput() { 
		connectSsh.setOutput(new StringBuilder())
		render ''
	}

	def closeConnection() {
		connectSsh?.closeConnection()
		log.info "Connection closed"
		redirect(url: session.referrer ?: '/')
	}

	def inspection() {
		def input = connectSsh.output
		render "${input}"
	}

	def inspect() {
		def input = connectSsh.output
		[input:input ]
	}
}
