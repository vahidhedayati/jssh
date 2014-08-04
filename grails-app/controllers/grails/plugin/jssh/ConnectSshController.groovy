package grails.plugin.jssh

class ConnectSshController {
	def connectSsh
	def jsshConfig
	def grailsApplication
	
	def index() {
		def process=grailsApplication.config.jssh.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock=grailsApplication.config.jssh.hideAuthBlock 
		[hideAuthBlock:hideAuthBlock]
	}
	
	
	def socketremote() {
		def process=grailsApplication.config.jssh.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
	}

	def ajaxpoll() {
		def process=grailsApplication.config.jssh.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
	}
	
	def socketprocess() {
		String hostname=params.hostname ?: 'localhost'
		String username=params.username
		String userCommand=params.command
		String password=params.password
		String template=params.template
		String port=params.port
		String divId=params.divId ?: 'Basic'
		String remoteForm=params.remoteForm
		def wshostname=grailsApplication.config.jssh.wshostname ?: 'localhost:8080'
		def hideConsoleMenu=grailsApplication.config.jssh.hideConsoleMenu ?: 'NO'
		def hideSendBlock=grailsApplication.config.jssh.hideSendBlock ?: 'NO'
		def hideSessionCtrl=grailsApplication.config.jssh.hideSessionCtrl ?: 'NO'
		def hideWhatsRunning=grailsApplication.config.jssh.hideWhatsRunning ?: 'NO'
		def hideDiscoButton=grailsApplication.config.jssh.hideDiscoButton ?: 'NO'
		def hidePauseControl=grailsApplication.config.jssh.hidePauseControl ?: 'NO'
		
		if (template) {
			render (view: 'getTemplate', model: [hideWhatsRunning:hideWhatsRunning,hideDiscoButton:hideDiscoButton,hidePauseControl:hidePauseControl,hideSessionCtrl:hideSessionCtrl,hideConsoleMenu:hideConsoleMenu,hideSendBlock:hideSendBlock,divId:divId,username:username,port:port,password:password,hostname:hostname,userCommand:userCommand,template:template,wshostname:wshostname])
		}else{
			if ( (remoteForm) && (remoteForm.equals('true'))) { 
				render (template: "/connectSsh/socketprocess", model:[hideWhatsRunning:hideWhatsRunning,hideDiscoButton:hideDiscoButton,hidePauseControl:hidePauseControl,hideSessionCtrl:hideSessionCtrl,hideConsoleMenu:hideConsoleMenu,hideSendBlock:hideSendBlock,divId:divId, username:username,port:port,password:password,hostname:hostname,userCommand:userCommand,wshostname:wshostname])
			}else{
				render (view: "/connectSsh/choose", model:[hideWhatsRunning:hideWhatsRunning,hideDiscoButton:hideDiscoButton,hidePauseControl:hidePauseControl,hideSessionCtrl:hideSessionCtrl,hideConsoleMenu:hideConsoleMenu,hideSendBlock:hideSendBlock,divId:divId,loadtemplate: 'socketprocess', username:username,port:port,password:password,hostname:hostname,userCommand:userCommand,wshostname:wshostname])
			}
		}
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
