package grails.plugin.jssh

import grails.converters.JSON

import org.springframework.dao.DataIntegrityViolationException

class ConnectSshController extends JsshConfService {

	def connectSsh
	def jsshRandService
	def jsshDbStorageService

	// _navbar.gsp menu map
	private Map menuMap = ['index':'Socket method', 'ajaxpoll':'Ajax poll',
		'socketremote':'RemoteForm Websocket', 'scsocket':'NEW: Websocket Client/Server']

	private boolean loadBootStrap, loadJQuery, loadStyle

	// Original websocket
	def index() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock = config.hideAuthBlock
		verifyBooleans(params)
		[hideAuthBlock:hideAuthBlock, process:process, menuMap:menuMap, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
	}

	// Original websocket using remoteForm
	def socketremote() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
		verifyBooleans(params)
		[process:process, menuMap:menuMap, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
	}

	// Socket Client/Server action
	def scsocket() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		verifyBooleans(params)
		[process:process, menuMap:menuMap, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
	}

	// Shared process feature - that determines what view to load according to form posted
	// by relevant action
	def process	(String jsshUser, String hostname, String username, String userCommand,
			String port, String password, String divId, String sshTitle, String view) {
		if (!divId) {
			divId = jsshRandService.shortRand('divId')
		}
		if (!sshTitle) {
			sshTitle = "Grails Jssh plugin: ${view}"
		}
		verifyBooleans(params)
		Map map =  [jsshUser: jsshUser, hostname: hostname, username: username, userCommand:userCommand,
			password: password, divId: divId, sshTitle: sshTitle, menuMap:menuMap, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
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
		verifyBooleans(params)
		[process:process, menuMap:menuMap, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
	}
	// End of Ajax polling related actions

	// Admin DB related features
	def addGroup() {
		render template: '/jsshadmin/addGroup'
	}

	def loadGroup(String username, String template) {
		JsshUser ju = JsshUser.findByUsername(username)
		def serverList
		def sshUserList
		if (ju) {
			serverList = SshServerGroups.findAllByUser(ju)
			sshUserList = ju.sshuser
		}
		render template: '/jsshadmin/'+template, model: [template:template, serverList:serverList, username: username, sshUserList: sshUserList]
	}

	
	def cloneUser(String jsshUsername) {
		render template: '/jsshadmin/cloneUser', model: [username: jsshUsername]
	}
	

	def cloneAccount(String username, String masteruser) {
		render jsshDbStorageService.cloneUser(username, masteruser) as String
	}
	
	
	def groupConnection(String jsshUsername, String groupId, String userCommand) {
		def grps = SshServerGroups.get(groupId)
		def  servers = grps.servers
		//def user = grps.user
		def jobName = jsshRandService.shortRand('job')
		def divId =  jsshRandService.shortRand('divId')

		Map model = [jsshUsername:jsshUsername , servers:servers, jobName:jobName,
			userCommand:userCommand, divId:divId, hideWhatsRunning: params.hideWhatsRunning,	hideDiscoButton: params.hideDiscoButton, 
			hidePauseControl:  params.hidePauseControl,	hideSessionCtrl:  params.hideSessionCtrl, hideConsoleMenu:  params.hideConsoleMenu, 
			hideSendBlock:  params.hideSendBlock, hideBroadCastBlock: params.hideBroadCastBlock]

		render template: '/jsshadmin/groupConnect', model: model
	}

	def loadServers(String username, String gId) {
		JsshUser ju = JsshUser.findByUsername(username)
		def serverList
		if (ju) {
			//def ss = SshServerGroups.findAllByIdAndUser(ju, gId)
			def ss = SshServerGroups.get(gId)
			if (ss.user == ju) {
				serverList = ss.servers
			}
		}
		if (serverList) {
			render template: '/jsshadmin/loadServers', model: [serverList:serverList]
		}else{
			render ""
		}
	}



	def loadList(String username, String sshId, String listType) {
		SshUser su = SshUser.get(sshId)
		def currentList
		if (su) {
			if (listType == "rewrite") {
				currentList = su.rewrite
			}else{
				currentList = su.blacklist
			}
		}
		if (currentList) {
			render template: '/jsshadmin/loadList', model: [currentList:currentList, listType: listType]
		}else{
			render ""
		}
	}

	def findSshCommand(String type, String command) {
		render jsshDbStorageService.findSshCommand(type, command) as JSON
	}

	def autoBlackList(String username, String cmd, String sshId) {
		render jsshDbStorageService.autoBlackList(username, cmd, sshId) as JSON
	}

	def autoRewriteList(String username, String cmd, String sshUserId) {
		Map map = [username:username, command:cmd, sshUserId:sshUserId]
		render template: '/jsshadmin/addRewriteRule', model: map
	}


	def addRewriteList(String sshuserId) {
		def rewriteList = params.rewrite
		if (rewriteList) {
			if(rewriteList instanceof String) {
				rewriteList = [rewriteList]
			}else{
				rewriteList = rewriteList as ArrayList
			}
			ArrayList rewriteLists = rewriteList
			jsshDbStorageService.addRewriteLists(sshuserId, rewriteLists)
			render "GroupID  ${rewriteLists} added to ${sshuserId}"
		}
	}


	def addRewriteDetails(String username, String command, String sshUserId, String replacement) {
		render jsshDbStorageService.addRewriteEntry(username, sshUserId, command, replacement) as String
	}



	def addHostDetails(String username, String hostName, String ip, String port) {
		render jsshDbStorageService.addServer(username, hostName, port ?: '22', ip ?: '') as String
	}

	def addHostDetail(String username, String hostName, String ip, String port, String groupId) {
		render jsshDbStorageService.addServer(username, hostName, port ?: '22', ip ?: '', groupId) as String
	}

	def addHostName(String username, String hostName, String groupId) {
		Map map = [hostName:hostName, username:username, groupId:groupId]
		render template: '/jsshadmin/addHost', model: map
	}

	def addjsshUser(String username, String permission) {
		SshServers sg
		jsshDbStorageService.addJsshUser(username, sg, permission)
	}
	
	def adduserDetails(String friendlyName, String username, String sshUsername, String sshKey, String sshKeyPass) {
		//def serverId = SshServers.getAll(params.list('serverId'))
		ArrayList serverId=params.list('serverId')
		if (!serverId && params.groupId) {
			def ssg = SshServerGroups.get(params.groupId)
			serverId = ssg.servers.id as ArrayList
		}
		jsshDbStorageService.addSShUser(friendlyName, username, sshUsername, sshKey, serverId)
		render "SSH User has been added"
	}

	def addGroupDetails(String username, String name) {
		jsshDbStorageService.storeGroup(name, username)
		render "Group should now be added"
	}

	def addGroupServers(String groupId) {
		def serverList = params.serverList
		if (serverList) {
			if(serverList instanceof String) {
				serverList = [serverList]
			}else{
				serverList = serverList as ArrayList
			}
			ArrayList serverLists = serverList
			jsshDbStorageService.addGroupServers(groupId, serverLists)
			render "GroupID  ${groupId} added to ${serverList}"
		}
	}

	def findServer(String hostName) {
		render jsshDbStorageService.findServer(hostName) as JSON
	}

	def findSshUser(String username) {
		render jsshDbStorageService.findSshUser(username) as JSON
	}

	// Logging/debugging utils
	def activeUsers() {
		jsshDbStorageService.activeUsers()
		render ""
	}

	//Admin options
	def siteAdmin(Integer max, String lookup) {
		if (session.isAdmin && session.isAdmin.toBoolean()) {
			verifyBooleans(params)
			params.max = Math.min(max ?: 50, 100)
			def userInstanceList, userInstanceTotal

			if (lookup == "jsshUser") {
				if (params.id) {
					userInstanceList = JsshUser?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = JsshUser.list(params)
					userInstanceTotal = JsshUser.count()
				}
			} else if (lookup == "sshUser") {
				if (params.id) {
					userInstanceList = SshUser?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = SshUser.list(params)
					userInstanceTotal = SshUser.count()
				}
 
			} else if (lookup == "sshServers") {
				if (params.id) {
					userInstanceList = SshServers?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = SshServers.list(params)
					userInstanceTotal = SshServers.count()
				}
			} else if (lookup == "sshServerGroups") {
				if (params.id) {
					userInstanceList = SshServerGroups?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = SshServerGroups.list(params)
					userInstanceTotal = SshServerGroups.count()
				}
			} else if (lookup == "sshCommandBlackList") {
				if (params.id) {
					userInstanceList = SshCommandBlackList?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = SshCommandBlackList.list(params)
					userInstanceTotal = SshCommandBlackList.count()
				}
			} else if (lookup == "sshCommandRewrite") {
				if (params.id) {
					userInstanceList = SshCommandRewrite?.get( params.id)
					userInstanceTotal = 1
				}else{
					userInstanceList = SshCommandRewrite.list(params)
					userInstanceTotal = SshCommandRewrite.count()
				}
			}
			

			String template = "/jsshadmin/view_${lookup}"
			Map model = [userInstanceList: userInstanceList, userInstanceTotal:userInstanceTotal,  lookup:lookup,
				loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]

			render template: template, model: model
			return
		}
		render "Acess denied"
		return
	}


	def edit(Long id, String table) {
		def uiterator
		if (table == "jsshUser") {
			uiterator = JsshUser.get(id)
		}else if (table == "sshUser") {
			uiterator = SshUser.get(id)
		}else if (table == "sshServers") {
			uiterator = SshServers.get(id)
		}else if (table == "sshServerGroups") {
			uiterator = SshServerGroups.get(id)
		}else if (table == "sshCommandBlackList") {
			uiterator = SshCommandBlackList.get(id)
		}else if (table == "sshCommandRewrite") {
			uiterator = SshCommandRewrite.get(id)
		}
		
		verifyBooleans(params)
		if (uiterator) {
			Map model = [table:table, uiterator:uiterator, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
			render template: "/jsshadmin/edit", model: model
			return
		}
		render "Invalid selection"
		return
	}


	def update(Long id,  Long version, String table) {

		def uiterator
		if (table == "jsshUser") {
			uiterator = JsshUser.get(id)
		}else if (table == "sshUser") {
			uiterator = SshUser.get(id)
		}else if (table == "sshServers") {
			uiterator = SshServers.get(id)
		}else if (table == "sshServerGroups") {
			uiterator = SshServerGroups.get(id)
		}else if (table == "sshCommandBlackList") {
			uiterator = SshCommandBlackList.get(id)
		}else if (table == "sshCommandRewrite") {
			uiterator = SshCommandRewrite.get(id)
		}
		 Map model = [table:table, uiterator:uiterator, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]

		if (!uiterator) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: table+'.label', default: table), id])
			redirect(action: "edit", params:params)
			return
		}

		if (version != null) {
			if (uiterator.version > version) {
				uiterator.errors.rejectValue("version", "default.optimistic.locking.failure",
						[message(code: table+'.label', default: table)] as Object[],
						"Another user has updated this Applications while you were editing")
				render(template: "/jsshadmin/edit", model:model)
				return
			}
		}

		uiterator.properties = params

		if (!uiterator.save(flush: true)) {
			render(template: "/jsshadmin/edit",  model: model)
			return
		}

		flash.message = message(code: 'default.updated.message', args: [message(code: table+'.label', default: table), uiterator.id])
		//redirect(action: "show", id: applicationsInstance.id)
		render(template: "/jsshadmin/edit",  model: model)
	}
	
	
	def delete(Long id, String table) { 
		
		def uiterator
		if (table == "jsshUser") {
			uiterator = JsshUser.get(id)
		}else if (table == "sshUser") {
			uiterator = SshUser.get(id)
		}else if (table == "sshServers") {
			uiterator = SshServers.get(id)
		}else if (table == "sshServerGroups") {
			uiterator = SshServerGroups.get(id)
		}else if (table == "sshCommandBlackList") {
			uiterator = SshCommandBlackList.get(id)
		}else if (table == "sshCommandRewrite") {
			uiterator = SshCommandRewrite.get(id)
		}
		
		Map model = [table:table, lookup:table, uiterator:uiterator, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
		if (!uiterator) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: table+'.label', default: table), id])
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
			return
		}

		try {
			uiterator.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: table+'.label', default: table), id])
			//render(template: "/jsshadmin/siteAdmin",  model: model)
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
		}catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: table+'.label', default: table), id])
			//render(template: "/jsshadmin/siteAdmin",  model: model)
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
			
		}
		
	}
	
	private verifyBooleans(params){
		this.loadBootStrap = (params.loadBootStrap as Boolean) ?: true
		this.loadJQuery = (params.loadJQuery as Boolean) ?: true
		this.loadStyle = (params.loadStyle as Boolean) ?: true
	}

}
