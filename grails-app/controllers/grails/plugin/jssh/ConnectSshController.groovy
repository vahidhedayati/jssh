package grails.plugin.jssh

class ConnectSshController extends ConfService {
	def connectSsh
	def randService
	//def jsshConfig
	Map menuMap = ['index':'Socket method', 'ajaxpoll':'Ajax poll', 
		'socketremote':'RemoteForm Websocket', 'scsocket':'NEW: Websocket Client/Server']
	
	def index() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock = config.hideAuthBlock
		[hideAuthBlock:hideAuthBlock, process:process, menuMap:menuMap]
	}
	
	
	def socketremote() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
		[process:process, menuMap:menuMap]
	}

	def ajaxpoll() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
	}

	
	def scsocket() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
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
		String wshostname = config.wshostname ?: 'localhost:8080'
		String hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		String hideSendBlock = config.hideSendBlock ?: 'YES'
		String hideSessionCtrl = config.hideSessionCtrl ?: 'YES'
		String hideWhatsRunning = config.hideWhatsRunning ?: 'NO'
		String hideDiscoButton = config.hideDiscoButton ?: 'NO'
		String hidePauseControl = config.hidePauseControl ?: 'NO'
		String hideNewShellButton = config.hideNewShellButton ?: 'YES'
		
		String addAppName =  config.addAppName ?: 'YES'
		String jobName,jsshUser
		if (!jobName) {
			jobName=randService.shortRand('job')
		}
		
		if (!jsshUser) {
			jsshUser=randService.randomise('jsshUser')
		}
		
		String uri="ws://${wshostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}
		
		//boolean frontend = params.frontend.toBoolean() ?: false
		boolean frontend = false
		Map model= [hideNewShellButton:hideNewShellButton, hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, 
			hidePauseControl:hidePauseControl, hideSessionCtrl:hideSessionCtrl, hideConsoleMenu:hideConsoleMenu, hideSendBlock:hideSendBlock,
			divId:divId, username:username, port:port, password:password, hostname:hostname, userCommand:userCommand, wshostname:wshostname, 
			addAppName:addAppName, jobName:jobName, jsshUser:jsshUser, frontend:frontend, uri:uri]
		
		if (template) {
			model.put('template',template)
			render (view: 'getTemplate', model: model )
		}else{
			if ( (remoteForm) && (remoteForm.equals('true'))) { 
				render (template: "/connectSsh/socketprocess", model:model)
			}else{
				model.put('loadtemplate', 'socketprocess')
				render (view: "/connectSsh/choose", model:model)
			}
		}
	}
	
	def scsocketconnect(String jsshUser, String hostname, String username, String userCommand, 
		String port, String password, String divId, String sshTitle) {
		if (!divId) {
			divId = randService.shortRand('divId')
		}
		if (!sshTitle) {
			sshTitle = "Jssh Websocket[Server/Client] SSH Connection"
		}
		[jsshUser: jsshUser, hostname: hostname, username: username, userCommand:userCommand, 
			password: password, divId: divId, sshTitle: sshTitle]
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
			model.put('template',template)
			render (view: 'getTemplate', model: model)
		}else{
			model.put('loadtemplate', 'process')
			render (view: "/connectSsh/choose", model: model)
		}
	}

	//private getConfig() { 
	//	grailsApplication.config?.jssh
	//}
	
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
