package grails.plugin.jssh

import grails.converters.JSON

class ConnectSshController extends ConfService {

	def connectSsh
	def randService
	def dbStorageService
	def dnsService

	// _navbar.gsp menu map
	private Map menuMap = ['index':'Socket method', 'ajaxpoll':'Ajax poll',
		'socketremote':'RemoteForm Websocket', 'scsocket':'NEW: Websocket Client/Server']

	// Original websocket
	def index() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock = config.hideAuthBlock
		[hideAuthBlock:hideAuthBlock, process:process, menuMap:menuMap]
	}

	// Original websocket using remoteForm
	def socketremote() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
		[process:process, menuMap:menuMap]
	}

	// Socket Client/Server action
	def scsocket() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
	}

	// Shared process feature - that determines what view to load according to form posted
	// by relevant action
	def process	(String jsshUser, String hostname, String username, String userCommand,
			String port, String password, String divId, String sshTitle, String view) {
		if (!divId) {
			divId = randService.shortRand('divId')
		}
		if (!sshTitle) {
			sshTitle = "Grails Jssh plugin: ${view}"
		}

		Map map =  [jsshUser: jsshUser, hostname: hostname, username: username, userCommand:userCommand,
			password: password, divId: divId, sshTitle: sshTitle, menuMap:menuMap]

		render view:view, model: map
	}


	// Ajax Polling related actions
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

	def ajaxpoll() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
	}
	// End of Ajax polling related actions

	// Admin DB related features
	def addGroup() {
		render template: '/admin/addGroup'
	}

	def loadGroup(String username, String template) {
		JsshUser ju = JsshUser.findByUsername(username)
		def serverList
		if (ju) {
			serverList = SshServerGroups.findByUser(ju)
		}
		render template: '/admin/'+template, model: [serverList:serverList]
	}

	def loadServers(String username, String gId) {
		JsshUser ju = JsshUser.findByUsername(username)
		def serverList
		if (ju) {
			def ss = SshServerGroups.findByUser(ju)
			serverList = ss.servers
		}
		render template: '/admin/loadServers', model: [serverList:serverList]
	}

	def addHostDetails(String username, String hostName, String ip, String port) {
		render dbStorageService.addServer(username, hostName, port ?: '22',ip ?: '') as String
	}

	def addHostDetail(String username, String hostName, String ip, String port, String groupId) {
		render dbStorageService.addServer(username, hostName, port ?: '22',ip ?: '',groupId) as String
	}

	def addHostName(String username, String hostName, String groupId) {
		Map map = [hostName:hostName, username:username, groupId:groupId]
		render template: '/admin/addHost', model: map
	}

	def addGroupServers(String groupId) {
		def serverList = params.serverList
		if(serverList instanceof String) {
			serverList = [serverList]
		}else{
			serverList = serverList as ArrayList
		}

		ArrayList serverLists = serverList
		dbStorageService.addGroupServers(groupId, serverLists)
		render "GroupID  ${groupId} added to ${serverList}"
	}

	def findServer(String hostName) {
		render dbStorageService.findServer(hostName) as JSON
	}

	// Logging/debugging utils
	def activeUsers() {
		dbStorageService.activeUsers()
		render ""
	}

}
