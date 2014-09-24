package grails.plugin.jssh
import javax.swing.text.html.HTML
class ConnectSshTagLib {
	static namespace = "jssh"
	def connectSsh
	def jsshConfig
	def grailsApplication
	def pluginbuddyService
	
	def ajaxconnect =  { attrs, body ->
		def hostname=attrs.remove('hostname')?.toString()
		def username=attrs.remove('username')?.toString()
		def userCommand=attrs.remove('userCommand') as HTML
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
		//def userCommand=attrs.remove('userCommand') as
		//def userCommand=attrs.remove('userCommand')?.encodeAsRaw()
		def password=attrs.remove('password')?.toString()
		def template=attrs.remove('template')?.toString()
		def port=attrs.remove('port')?.toString()
		def divId=attrs.remove('divId')?.toString()
		
		def wshostname
		if (!attrs.wshostname) {
			wshostname=grailsApplication.config.jssh.wshostname ?: 'localhost:8080'
		}else{
			wshostname=attrs.wshostname
		}	
		
		def hideConsoleMenu
		if (!attrs.hideConsoleMenu) {
			hideConsoleMenu=grailsApplication.config.jssh.hideConsoleMenu ?: 'NO'
		}else{
			hideConsoleMenu=attrs.hideConsoleMenu
		}
			
		def hideSendBlock
		if (!attrs.hideSendBlock) {
			hideSendBlock=grailsApplication.config.jssh.hideSendBlock ?: 'YES'
		}else{
			hideSendBlock=attrs.hideSendBlock
		}
		
		def hideSessionCtrl
		if (!attrs.hideSessionCtrl)	 {
			hideSessionCtrl=grailsApplication.config.jssh.hideSessionCtrl ?: 'YES'
		}else{
			hideSessionCtrl=attrs.hideSessionCtrl
		}
		
		def hideWhatsRunning
		if (!attrs.hideWhatsRunning) {
			hideWhatsRunning=grailsApplication.config.jssh.hideWhatsRunning ?: 'NO'
		}else{
			hideWhatsRunning=attrs.hideWhatsRunning
		}
			
		def hideDiscoButton
		if (!attrs.hideDiscoButton) {
			hideDiscoButton=grailsApplication.config.jssh.hideDiscoButton ?: 'NO'
		}else{
			hideDiscoButton=attrs.hideDiscoButton
		}
		
		def hidePauseControl
		if (!attrs.hidePauseControl) {
			hidePauseControl=grailsApplication.config.jssh.hidePauseControl ?: 'NO'
		}else{
			hidePauseControl=attrs.hidePauseControl
		} 
		
		def hideNewShellButton
		if (!attrs.hideNewShellButton) {
			hideNewShellButton=grailsApplication.config.jssh.hideNewShellButton ?: 'YES'
		}else{
			hideNewShellButton=attrs.hideNewShellButton
		}
		
		
		
		if (template) {
			out << g.render(template:template, model: [hideNewShellButton:hideNewShellButton,hideWhatsRunning:hideWhatsRunning,hideDiscoButton:hideDiscoButton,hidePauseControl:hidePauseControl,hideSessionCtrl:hideSessionCtrl,hideConsoleMenu:hideConsoleMenu,hideSendBlock:hideSendBlock,divId:divId,wshostname:wshostname,hostname:hostname,port:port,password:password,username:username,userCommand:attrs.userCommand])
		}else{
			out << g.render(contextPath: pluginContextPath,template:"/connectSsh/socketprocess", model: [hideWhatsRunning:hideWhatsRunning,hideDiscoButton:hideDiscoButton,hidePauseControl:hidePauseControl,hideSessionCtrl:hideSessionCtrl,hideConsoleMenu:hideConsoleMenu,hideSendBlock:hideSendBlock,divId:divId,wshostname:wshostname,hostname:hostname,port:port,password:password,username:username,userCommand:attrs.userCommand.encodeAsHTML()])
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
		def hideAuthBlock=attrs.remove('hideAuthBlock')?.toString()
		if (!pageid) { 
			pageid="jssh"
		}
		if (!pagetitle) {
			pagetitle="jssh J2SSH Websocket/Ajax plugin"
		}
		
		def gfolder=pluginbuddyService.returnAppVersion()
		out << g.render(contextPath: pluginContextPath, template: "/connectSsh/${gfolder}/${file}", model: [hideAuthBlock:hideAuthBlock,attrs:attrs,loadtemplate:loadtemplate,input:input,port:port,username:username,password:password,hostname:hostname,userCommand:userCommand,pageid:pageid,pagetitle:pagetitle])
	}

}
