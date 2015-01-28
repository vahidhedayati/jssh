

package grails.plugin.jssh

import grails.plugin.jssh.logging.CommandLogger

import javax.websocket.Session

class ConnectSshTagLib extends ConfService {
	static namespace = "jssh"

	def connectSsh
	def jsshConfig
	def pluginbuddyService
	def randService
	def clientListenerService
	def dbStorageService

	private ArrayList hosts

	private String hostname, username, userCommand, password, template, port, adminTemplate, sshKey, sshKeyPass

	private String wshostname, hideConsoleMenu, hideSendBlock, hideSessionCtrl,
	hideWhatsRunning, hideDiscoButton, hidePauseControl, hideNewShellButton, jobName,
	jsshUser, divId, enablePong, pingRate, addAppName

	private String jUser, conLogId, conloggerId, comloggerId

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

		socketOpts(attrs)

		boolean frontend = attrs.remove('frontend')?.toBoolean() ?: false
		String uri="ws://${wshostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}
		def model = [hideWhatsRunning:hideWhatsRunning, hideDiscoButton:hideDiscoButton, hidePauseControl:hidePauseControl,
			hideSessionCtrl:hideSessionCtrl, hideNewShellButton:hideNewShellButton, hideConsoleMenu:hideConsoleMenu,
			hideSendBlock:hideSendBlock, wshostname:wshostname, hostname:hostname, port:port, addAppName:addAppName,
			username:username, password:password, userCommand:userCommand, divId:divId, uri:uri,
			enablePong:enablePong, pingRate:pingRate, jobName:jobName, jsshUser:jsshUser, frontend:frontend]

		loadTemplate(template, "/connectSsh/socketprocess", model)
	}


	/* jssh:broadcast only required on top of jssh:conn if used multiple times on 1 page
	 * will send a broadcast message to all the connections
	 * All JobNames on jssh:conn must match the same jobName as broadcast for this
	 * Provide divId + jobName + template(optional) 
	 */ 
	def broadcast = { attrs ->
		/*
		this.jobName = attrs.remove('jobName')?.toString()
		
		if (!jobName) {
			throwTagError("Tag [broadcast] is missing required attribute [jobName]")
		}
		*/
		
		socketOpts(attrs)
		String uri="ws://${wshostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}
		uri = uri + jobName
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
		genDb(username, hosts, hostname, jsshUser, userCommand, port, sshKey, sshKeyPass)

		def cuser= jsshUser

		def cjob = jobName
		String frontuser = cuser+frontend


		boolean frontend = true
		String uri="ws://${wshostname}/${appName}/${APP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${APP}/"
		}

		uri = uri + cjob
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
			loadTemplate(template,'/connectSsh/socketConnect', connMap)
			
			loadBackEnd(uri, connMap, cuser)
		}
		// TODO -
		/*	
		 if (hosts) {
		 hosts.each { hostn ->
		 connMap.put('hostname', hostn)
		 loadTemplate(template,'/connectSsh/socketConnect', connMap)
		 loadBackEnd(uri, connMap, cuser)
		 }
		 }
		 */
	}

	private void loadBackEnd(String uri, Map connMap, String cuser) {
		connMap.remove('jsshUser')
		connMap.put('jsshUser', cuser)
		if (jUser) {
			connMap.put('jUser', jUser)
		}
		connMap.put('conLogId', conLogId)
		connMap.put('conloggerId', conloggerId)
		connMap.put('comloggerId', comloggerId)
		connMap.put('hostname', hostname)
		connMap.put('sshKey', sshKey)
		connMap.put('sshKeyPass', sshKeyPass)
		
		if (hosts) {
			connMap.put('hosts', hosts)
		}
		Session oSession = clientListenerService.p_connect(uri, cuser, connMap)
	}

	private void loadTemplate(String template, String pluginTemplate, Map model) {
		if (template) {
			out << g.render(template:template, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:pluginTemplate, model: model)
		}
	}

	private void genericOpts(attrs) {
		if (attrs.hostname && (!attrs.hosts)) {
			this.hostname = attrs.remove('hostname')?.toString() ?: 'localhost'
		}

		if (attrs.hosts instanceof ArrayList) {
			this.hosts = attrs.hostname
		}

		this.sshKey = attrs.remove('sshKey')?.toString() ?: config.KEY ?: ''
		this.sshKeyPass = attrs.remove('sshKeyPass')?.toString() ?: config.KEYPASS ?: ''
		this.username = attrs.remove('username')?.toString() ?: config.USER ?: ''
		this.userCommand = attrs.remove('userCommand') ?: "echo \"\$USER has logged into \$HOST\""
		this.password = attrs.remove('password')?.toString()
		this.template = attrs.remove('template')?.toString()
		this.adminTemplate = attrs.remove('adminTemplate')?.toString() ?: ''
		this.port = attrs.remove('port')?.toString() ?: '22'
	}

	private void socketOpts(attrs) {
		genericOpts(attrs)
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
			jobName=randService.shortRand('job')
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

	private void genDb(String sshUser, ArrayList hosts, String hostname, String username, String userCommand, String port, 
		String sshKey=null, String sshKeyPass=null) {
		if (hosts) {
			hosts.each { host ->
				addUserHost(host, port, username, sshUser, sshKey, sshKeyPass)
			}
		}else{
			addUserHost(hostname, port, username, sshUser, sshKey, sshKeyPass)
		}
		this.conloggerId = dbStorageService.storeConnection(hostname, port, username, sshUser, conLogId)
		this.comloggerId = dbStorageService.storeCommand(userCommand, username, conLogId, sshUser)
	}

	private void addUserHost(String hostname, String port, String username, String sshUser, String sshKey=null, String sshKeyPass=null) {
		SshServers server = dbStorageService.addServer(username, hostname, port ?: '22', '' )
		def SshUser =  dbStorageService.addSShUser(username, sshUser, sshKey ?: '', sshKeyPass ?: '', server.id as String )
		Map<String,String> jU = dbStorageService.addJsshUser(username, server)
		this.jUser = jU.user
		this.conLogId = jU.conId
	}
}
