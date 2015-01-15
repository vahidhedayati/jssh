package grails.plugin.jssh
class ConnectSshTagLib {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	def grailsApplication
	def pluginbuddyService

	def ajaxconnect =  { attrs, body ->
		def hostname = attrs.remove('hostname')?.toString()
		def username = attrs.remove('username')?.toString()
		def userCommand = attrs.remove('userCommand')
		def password = attrs.remove('password')?.toString()
		def template = attrs.remove('template')?.toString()
		def port = attrs.remove('port')?.toString()

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
		
		session.referrer = request.getHeader('referer')
		
		def asyncProcess = new Thread({	connectSsh.ssh(jsshConfig)  } as Runnable )
		asyncProcess.start()
		
		def input = connectSsh.output
		
		Map model = [input:input, port:port, hostname:hostname, userCommand:userCommand]
		
		if (template) {
			out << g.render(template:template, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:'/connectSsh/process', model: model)
		}
	}

	def socketconnect =  { attrs, body ->
		def hostname = attrs.remove('hostname')?.toString()
		def username = attrs.remove('username')?.toString()
		def userCommand = attrs.remove('userCommand')
		def password = attrs.remove('password')?.toString()
		def template = attrs.remove('template')?.toString()
		def port = attrs.remove('port')?.toString()
		def divId = attrs.remove('divId')?.toString()
		def enablePong = attrs.remove('enablePong')?.toString() ?: config.enablePong 
		def pingRate = attrs.remove('pingRate')?.toString() ?: config.pingRate
		def pingInterval = attrs.remove('pingInterval')?.toString() ?: config.pingInterval
		def pingMessage = attrs.remove('pingMessage')?.toString() ?: config.pingMessage

		String addAppName =  config.addAppName ?: 'YES'
		
		String wshostname
		if (!attrs.wshostname) {
			wshostname = config.wshostname ?: 'localhost:8080'
		}else{
			wshostname = attrs.wshostname
		}

		String hideConsoleMenu
		if (!attrs.hideConsoleMenu) {
			hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		}else{
			hideConsoleMenu = attrs.hideConsoleMenu
		}

		String hideSendBlock
		if (!attrs.hideSendBlock) {
			hideSendBlock = config.hideSendBlock ?: 'YES'
		}else{
			hideSendBlock = attrs.hideSendBlock
		}

		String hideSessionCtrl
		if (!attrs.hideSessionCtrl)	 {
			hideSessionCtrl = config.hideSessionCtrl ?: 'YES'
		}else{
			hideSessionCtrl = attrs.hideSessionCtrl
		}

		String hideWhatsRunning
		if (!attrs.hideWhatsRunning) {
			hideWhatsRunning = config.hideWhatsRunning ?: 'NO'
		}else{
			hideWhatsRunning = attrs.hideWhatsRunning
		}

		String hideDiscoButton
		if (!attrs.hideDiscoButton) {
			hideDiscoButton = config.hideDiscoButton ?: 'NO'
		}else{
			hideDiscoButton = attrs.hideDiscoButton
		}

		String hidePauseControl
		if (!attrs.hidePauseControl) {
			hidePauseControl = config.hidePauseControl ?: 'NO'
		}else{
			hidePauseControl = attrs.hidePauseControl
		}

		String hideNewShellButton
		if (!attrs.hideNewShellButton) {
			hideNewShellButton = config.hideNewShellButton ?: 'YES'
		}else{
			hideNewShellButton = attrs.hideNewShellButton
		}

		if (!userCommand) {
			userCommand="echo \"\$USER has logged into \$HOST\""
		}
		

		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, hidePauseControl:hidePauseControl, 
			hideSessionCtrl:hideSessionCtrl, hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu, 
			hideSendBlock:hideSendBlock, wshostname:wshostname, hostname:hostname, port:port, addAppName:addAppName, 
			username:username, password:password, userCommand:userCommand, divId:divId, enablePong:enablePong, pingRate:pingRate,
			pingInterval:pingInterval, pingMessage: pingMessage ]
		
		
		if (template) {
			out << g.render(template:template, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:"/connectSsh/socketprocess", model: model)
		}
	}

	def chooseLayout =  { attrs, body ->
		def file = attrs.remove('file')?.toString()
		def loadtemplate = attrs.remove('loadtemplate')?.toString()
		def hostname = attrs.remove('hostname')?.toString()
		def username = attrs.remove('username')?.toString()
		def userCommand = attrs.remove('userCommand')?.toString()
		def password = attrs.remove('password')?.toString()
		def template = attrs.remove('template')?.toString()
		def port = attrs.remove('port')?.toString()
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

	private getConfig() {
		grailsApplication.config?.jssh
	}
}
