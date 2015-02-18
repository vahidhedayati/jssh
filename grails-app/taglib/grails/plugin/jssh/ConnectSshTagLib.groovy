package grails.plugin.jssh

import javax.websocket.Session

class ConnectSshTagLib extends JsshConfService {
	
	static namespace = "jssh"

	def connectSsh
	def jsshConfig
	def pluginbuddyService
	def jsshRandService
	def jsshClientListenerService
	def jsshDbStorageService
	def jsshUserService
	
	private String 	hostname, username, userCommand, password, template, port, adminTemplate, sshKey, sshKeyPass, uri, wsprotocol

	private String 	wshostname, hideConsoleMenu, hideSendBlock, hideSessionCtrl,hideWhatsRunning, hideDiscoButton, hidePauseControl, 
					hideNewShellButton, jobName, jsshUser, realUser, divId, enablePong, pingRate, addAppName

	private String 	jUser, conLogId, conloggerId, comloggerId

	private Boolean loadBootStrap, loadJQuery
	
	
	def sshList = { attrs ->
		def ilist = attrs.remove('ilist')
		String rtype = attrs.remove('rtype').toString() 
		ArrayList finalList = []
		if (ilist instanceof String) {
			finalList = [ilist]
		}else{
			finalList = ilist as ArrayList
		}
		if (rtype == "sshuser") {
			finalList.each { SshUser user ->
				out << """<g:link controller="connectSsh" action="		${user.username} """	
			}
		}else if (rtype == "servers") {
			finalList.each { SshServers server ->
				out << """		${server.hostName} """	
			}
		}else if (rtype == "groups") {
			finalList.each { SshServerGroups groups ->
				out << """		${groups.name} """				
			}
		}else if (rtype == "rewrite") {
			finalList.each { SshCommandRewrite rewrite ->
				out << """		${rewrite.command} """				
			}
		}else if (rtype == "blacklist") {
			finalList.each { SshCommandBlackList blacklist ->
				out << """		${blacklist.command} """				
			}
		}
			
	}
	/*
	 * loadAdmin requires a userId
	 * once confirmed as admin - admin view shown on calling gsp
	 */
	def loadAdmin = { attrs ->
		String jsshUser = attrs.remove('jsshUser')?.toString()
		boolean loadBootStrap = attrs.remove('loadBootStrap')?.toBoolean() ?: true
		boolean loadJQuery = attrs.remove('loadJQuery')?.toBoolean() ?: true
		boolean loadStyle = attrs.remove('loadStyle')?.toBoolean() ?: true
		if (jsshUser && jsshUserService.isAdmin(jsshUser)) {
			session.jsshuser = jsshUser
			session.isAdmin = 'true'
			def model = [jsshUser:jsshUser, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery , loadStyle:loadStyle]
			loadTemplate(template, '/connectSsh/jsshAdmin', model)
		}
	}
	
	/* jssh:broadcast only required on top of jssh:conn if used multiple times on 1 page
	 * will send a broadcast message to all the connections
	 * All JobNames on jssh:conn must match the same jobName as broadcast for this
	 * Provide divId + jobName + template(optional)
	 */
	def broadcast = { attrs ->

		socketOpts(attrs)

		boolean frontend = true

		def model = [frontend:frontend, divId: divId, jobName: jobName,
			uri:uri, job: jobName,	wshostname: wshostname, frontuser: jsshUser, jsshUser: jsshUser]
		loadTemplate(template, '/connectSsh/broadcast', model)
	}



	/* NEW : Client / Server SSH Model - Server or Master being backend SSH connection / client
	 *  being websocket client
	 * That listens to Websocket server/master that sends connection info back to client
	 */

	def conn = { attrs ->

		socketOpts(attrs)

		// Only register commands and transactions from new conn method
		genDb(username, hostname, realUser, userCommand, port, sshKey, sshKeyPass)

		def cuser= jsshUser

		def cjob = jobName
		String frontuser = cuser+frontend


		boolean frontend = true

		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}

		def connMap = [frontend: frontend,  frontuser: frontuser,  backuser: cuser, user: username, username:username, password: password,
			port: port, enablePong: enablePong, pingRate: pingRate, usercommand: userCommand, divId:divId, jsshApp: APP, uri:uri, job: cjob,
			hideWhatsRunning: hideWhatsRunning,	hideDiscoButton: hideDiscoButton, hidePauseControl: hidePauseControl, hideSessionCtrl: hideSessionCtrl,
			hideNewShellButton: hideNewShellButton, hideConsoleMenu: hideConsoleMenu, hideSendBlock: hideSendBlock,	wshostname: wshostname]

		Map adminMap = [frontuser: frontuser, backuser: cuser, job:cjob]
		loadTemplate(adminTemplate,"/connectSsh/scsockmodal",adminMap)

		connMap.put('jsshUser', frontuser)
		if (hostname) {
			connMap.put('hostname', hostname)
			connMap.put('loadBootStrap',loadBootStrap)
			connMap.put('loadJQuery',loadJQuery)
			loadTemplate(template,'/connectSsh/socketConnect', connMap)

			loadBackEnd(uri, connMap, cuser)
		}

	}

	/*
	 * Older socket method
	 * Direct socket connection between browser and ssh connection
	 */
	def socketconnect =  { attrs ->

		socketOpts(attrs)

		boolean frontend = attrs.remove('frontend')?.toBoolean() ?: false

		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, hidePauseControl:hidePauseControl,
			hideSessionCtrl:hideSessionCtrl, hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu,
			hideSendBlock:hideSendBlock, wshostname:wshostname, hostname:hostname, port:port, addAppName:addAppName,
			username:username, password:password, userCommand:userCommand, divId:divId, uri:uri,
			enablePong:enablePong, pingRate:pingRate, jobName:jobName, jsshUser:jsshUser, frontend:frontend, 
			loadBootStrap:loadBootStrap, loadJQuery: loadJQuery]

		loadTemplate(template, "/connectSsh/socketprocess", model)
	}




	/* Ajax method of connection
	 * This is not recommended since it can only really be used once
	 */
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

	private void loadBackEnd(String uri, Map connMap, String cuser) {
		connMap.remove('jsshUser')
		connMap.put('jsshUser', cuser)
		if (jUser) {
			connMap.put('jUser', jUser)
		}
		connMap.put('realUser', realUser)
		connMap.put('conLogId', conLogId)
		connMap.put('conloggerId', conloggerId)
		connMap.put('comloggerId', comloggerId)
		connMap.put('hostname', hostname)
		connMap.put('sshKey', sshKey)
		connMap.put('sshKeyPass', sshKeyPass)
		connMap.put('enablePong', enablePong)
		connMap.put('pingRate', pingRate)

		Session oSession = jsshClientListenerService.p_connect(uri, cuser, connMap)
	}

	/* 
	 * load templates 
	 */
	private void loadTemplate(String template, String pluginTemplate, Map model) {
		if (template) {
			out << g.render(template:template, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:pluginTemplate, model: model)
		}
	}

	/* 
	 * Generic options used for all calls
	 */
	private void genericOpts(attrs) {
		if (attrs.hostname) {
			this.hostname = attrs.remove('hostname')?.toString() ?: 'localhost'
		}
		this.sshKey = attrs.remove('sshKey')?.toString() ?: config.KEY ?: ''
		this.sshKeyPass = attrs.remove('sshKeyPass')?.toString() ?: config.KEYPASS ?: ''
		this.username = attrs.remove('username')?.toString() ?: config.USER ?: ''
		this.userCommand = attrs.remove('userCommand') ?: "echo \"\$USER has logged into \$HOST\""
		this.password = attrs.remove('password')?.toString()
		this.template = attrs.remove('template')?.toString()
		this.adminTemplate = attrs.remove('adminTemplate')?.toString() ?: ''
		this.port = attrs.remove('port')?.toString() ?: '22'
		this.loadBootStrap = attrs.remove('loadBootStrap')?.toBoolean() ?: true
		this.loadJQuery = attrs.remove('loadJQuery')?.toBoolean() ?: true
	}


	/* Socket options 
	 * used by older / newer methods of socket connection	
	 */
	private void socketOpts(attrs) {

		genericOpts(attrs)

		this.addAppName =  config.addAppName ?: 'YES'

		this.wsprotocol = attrs.remove('wsprotocol')?.toString() ?: config.wsprotocol ?: 'ws'

		if (!attrs.wshostname) {
			this.wshostname = config.wshostname ?: 'localhost:8080'
		}else{
			this.wshostname = attrs.wshostname
		}

		this.jobName = attrs.remove('jobName')?.toString()

		if (!jobName) {
			jobName=jsshRandService.shortRand('job')
		}

		this.uri="${wsprotocol}://${wshostname}/${appName}/${APP}/${jobName}"
		if (addAppName=="no") {
			this.uri="${wsprotocol}://${wshostname}/${APP}/${jobName}"
		}

		if (!attrs.hideConsoleMenu) {
			this.hideConsoleMenu = config.hideConsoleMenu ?: 'NO'
		}else{
			this.hideConsoleMenu = attrs.hideConsoleMenu
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
			jsshUser=jsshRandService.randomise('jsshUser')
		}

		this.realUser = attrs.remove('realUser')?.toString()
		if (!realUser) {
			realUser = jsshUser
		}

		this.divId = attrs.remove('divId')?.toString()
		this.enablePong = attrs.remove('enablePong')?.toString() ?: config.enablePong
		// In milliseconds how to long to wait before sending next ping
		this.pingRate = attrs.remove('pingRate')?.toString() ?: config.pingRate ?: '60000'
	}

	/*
	 * Update DB with configuration from socket connections
	 */
	private void genDb(String sshUser, String hostname, String realuser, String userCommand, String port,
		String sshKey=null, String sshKeyPass=null) {
		addUserHost(hostname, port, realuser, sshUser, sshKey, sshKeyPass)
		this.conloggerId = jsshDbStorageService.storeConnection(hostname, port, realuser, sshUser, '')
		this.comloggerId = jsshDbStorageService.storeCommand(userCommand, realuser, conLogId, sshUser)
	}

	/*
	 * Part of genDb
	 */
	private void addUserHost(String hostname, String port, String realuser, String sshUser, String sshKey=null, String sshKeyPass=null) {
		SshServers server = jsshDbStorageService.addServer(realuser, hostname, port ?: '22', '' )
		def SshUser =  jsshDbStorageService.addSShUser(realuser, realuser, sshUser, sshKey ?: '', sshKeyPass ?: '', [server.id] as ArrayList )
		
		Map<String,String> jU = jsshDbStorageService.addJsshUser(realuser, server)
		this.jUser = jU.user
		this.conLogId = jU.conId
	}
}
