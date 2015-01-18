package grails.plugin.jssh

import javax.websocket.Session

class ConnectSshTagLib extends ConfService {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	def pluginbuddyService
	def randService
	def clientListenerService
	private String hostname, username, userCommand, password, template, port
	
	private String wshostname, hideConsoleMenu, hideSendBlock, hideSessionCtrl,
	hideWhatsRunning, hideDiscoButton, hidePauseControl, hideNewShellButton, jobName,
	jsshUser, divId, enablePong, pingRate, addAppName
	
	def chooseLayout =  { attrs ->
		genericOpts(attrs)
		def input = attrs.remove('input')?.toString()
		def pageid = attrs.remove('pageid')?.toString()
		def pagetitle = attrs.remove('pagetitle')?.toString()
		def hideAuthBlock = attrs.remove('hideAuthBlock')?.toString()
		if (!pageid) {
			pageid="jssh"
		}
		if (!pagetitle) {
			pagetitle="jssh J2SSH Websocket/Ajax plugin"
		}
		def gfolder = pluginbuddyService.returnAppVersion()
		Map model = [ hideAuthBlock:hideAuthBlock, attrs:attrs, loadtemplate:loadtemplate, input:input,
			port:port, username:username, password:password, hostname:hostname, userCommand:userCommand,
			pageid:pageid, pagetitle:pagetitle ]
		out << g.render(contextPath: pluginContextPath, template: "/connectSsh/${gfolder}/${file}", model: model)
	}
	
	def ajaxconnect =  { attrs ->
	
		genericOpts(attrs)
		
		if (username) {
			connectSsh.setUser(username)
		}

		if (password) {
			connectSsh.setUserpass(password)
		}

		connectSsh.setHost(hostname)
		connectSsh.setUsercommand(userCommand)
		
		session.referrer = request.getHeader('referer')
		
		def asyncProcess = new Thread({	connectSsh.ssh(jsshConfig)  } as Runnable )
		asyncProcess.start()
		
		def input = connectSsh.output
		
		Map model = [input:input, port:port, hostname:hostname, userCommand:userCommand]
		
		loadTemplate(template, '/connectSsh/process', model)
	}

	def socketconnect =  { attrs ->
		
		genericOpts(attrs)
		socketOpts(attrs)
		
		boolean frontend = attrs.remove('frontend')?.toBoolean() ?: false
		
		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, hidePauseControl:hidePauseControl, 
			hideSessionCtrl:hideSessionCtrl, hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu, 
			hideSendBlock:hideSendBlock, wshostname:wshostname, hostname:hostname, port:port, addAppName:addAppName, 
			username:username, password:password, userCommand:userCommand, divId:divId, 
			enablePong:enablePong, pingRate:pingRate, jobName:jobName, jsshUser:jsshUser, frontend:frontend]
		
		loadTemplate(template, "/connectSsh/socketprocess", model)	
	}

	def conn = { attrs ->
		genericOpts(attrs)
		socketOpts(attrs)
		
		boolean frontend = true
		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, hidePauseControl:hidePauseControl,
			hideSessionCtrl:hideSessionCtrl, hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu,
			hideSendBlock:hideSendBlock, wshostname:wshostname, hostname:hostname, port:port, addAppName:addAppName,
			username:username, password:password, userCommand:userCommand, divId:divId,
			enablePong:enablePong, pingRate:pingRate, jobName:jobName, jsshUser:jsshUser, frontend:frontend]
		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}

	
		if (!message) {
			message = "testing"
		}

		String uri="ws://${wshostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}

		// Make a socket connection as actual main user (backend connection)
		Session oSession = clientListenerService.p_connect(uri, jsshUser, jobName)

		// TODO
		loadTemplate(attrs,'socketConnect', model)
		
	}
	
	private void loadTemplate(String template, String pluginTemplate, Map model) {
		if (template) {
			out << g.render(template:template, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:pluginTemplate, model: model)
		}
	}
	
	private void genericOpts(attrs) {
		this.hostname = attrs.remove('hostname')?.toString()
		this.username = attrs.remove('username')?.toString()
		this.userCommand = attrs.remove('userCommand')
		this.password = attrs.remove('password')?.toString()
		this.template = attrs.remove('template')?.toString()
		this.port = attrs.remove('port')?.toString()
		
		if (!userCommand) {
			userCommand="echo \"\$USER has logged into \$HOST\""
		}
		
		if (!hostname) {
			throwTagError("Tag is missing required attribute [hostname]")
		}


	}
	
	private void socketOpts(attrs) {
		if (!attrs.wshostname) {
			this.wshostname = config.wshostname ?: 'localhost:8080'
		}else{
			this.wshostname = attrs.wshostname
		}
		
		if (!attrs.hideConsoleMenu) {
			this.hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		}else{
			this.hideConsoleMenu = attrs.hideConsoleMenu
		}
		
		this.jobName = attrs.remove('jobName')?.toString()
		
		if (!jobName) {
			jobName=randService.randomise('job')
		}
		
		if (!attrs.hideSendBlock) {
			this.hideSendBlock = config.hideSendBlock ?: 'YES'
		}else{
			this.hideSendBlock = attrs.hideSendBlock
		}

		if (!attrs.hideSessionCtrl)	 {
			this.hideSessionCtrl = config.hideSessionCtrl ?: 'YES'
		}else{
			this.hideSessionCtrl = attrs.hideSessionCtrl
		}

		if (!attrs.hideWhatsRunning) {
			this.hideWhatsRunning = config.hideWhatsRunning ?: 'NO'
		}else{
			this.hideWhatsRunning = attrs.hideWhatsRunning
		}

		if (!attrs.hideDiscoButton) {
			this.hideDiscoButton = config.hideDiscoButton ?: 'NO'
		}else{
			hideDiscoButton = attrs.hideDiscoButton
		}

		if (!attrs.hidePauseControl) {
			this.hidePauseControl = config.hidePauseControl ?: 'NO'
		}else{
			this.hidePauseControl = attrs.hidePauseControl
		}

		if (!attrs.hideNewShellButton) {
			this.hideNewShellButton = config.hideNewShellButton ?: 'YES'
		}else{
			this.hideNewShellButton = attrs.hideNewShellButton
		}
		
		this.jsshUser = attrs.remove('jsshUser')?.toString()
		if (!jsshUser) {
			jsshUser=randService.randomise('jsshUser')
		}
		
		this.divId = attrs.remove('divId')?.toString()
		this.enablePong = attrs.remove('enablePong')?.toString() ?: config.enablePong
		// In milliseconds how to long to wait before sending next ping
		this.pingRate = attrs.remove('pingRate')?.toString() ?: config.pingRate ?: '60000'
		
		this.addAppName =  config.addAppName ?: 'YES'
		
	}
	
}
