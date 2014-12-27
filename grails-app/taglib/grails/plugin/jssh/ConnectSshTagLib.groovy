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

		def wshostname
		if (!attrs.wshostname) {
			wshostname = config.wshostname ?: 'localhost:8080'
		}else{
			wshostname = attrs.wshostname
		}

		def hideConsoleMenu
		if (!attrs.hideConsoleMenu) {
			hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		}else{
			hideConsoleMenu = attrs.hideConsoleMenu
		}

		def hideSendBlock
		if (!attrs.hideSendBlock) {
			hideSendBlock = config.hideSendBlock ?: 'YES'
		}else{
			hideSendBlock = attrs.hideSendBlock
		}

		def hideSessionCtrl
		if (!attrs.hideSessionCtrl)	 {
			hideSessionCtrl = config.hideSessionCtrl ?: 'YES'
		}else{
			hideSessionCtrl = attrs.hideSessionCtrl
		}

		def hideWhatsRunning
		if (!attrs.hideWhatsRunning) {
			hideWhatsRunning = config.hideWhatsRunning ?: 'NO'
		}else{
			hideWhatsRunning = attrs.hideWhatsRunning
		}

		def hideDiscoButton
		if (!attrs.hideDiscoButton) {
			hideDiscoButton = config.hideDiscoButton ?: 'NO'
		}else{
			hideDiscoButton = attrs.hideDiscoButton
		}

		def hidePauseControl
		if (!attrs.hidePauseControl) {
			hidePauseControl = config.hidePauseControl ?: 'NO'
		}else{
			hidePauseControl = attrs.hidePauseControl
		}

		def hideNewShellButton
		if (!attrs.hideNewShellButton) {
			hideNewShellButton = config.hideNewShellButton ?: 'YES'
		}else{
			hideNewShellButton = attrs.hideNewShellButton
		}

		if (!userCommand) {
			userCommand="echo \"\$USER has logged into \$HOST\""
		}
		

		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton,
			hidePauseControl:hidePauseControl, hideSessionCtrl:hideSessionCtrl,
			hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu, 
			hideSendBlock:hideSendBlock, divId:divId, wshostname:wshostname, hostname:hostname, 
			port:port, password:password, username:username, userCommand:userCommand]
		
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
