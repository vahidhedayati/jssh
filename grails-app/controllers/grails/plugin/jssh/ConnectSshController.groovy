package grails.plugin.jssh

import grails.converters.JSON
import grails.plugin.jssh.j2ssh.JsshConfService
import grails.plugin.jssh.logging.CommandLogger
import grails.plugin.jssh.logging.CommandLogs
import grails.plugin.jssh.logging.ConnectionLogs

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

			def  aa = jsshDbStorageService.siteAdmin(lookup, params)
			def userInstanceList = aa.userInstanceList
			def userInstanceTotal = aa.userInstanceTotal

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
		def uiterator = jsshDbStorageService.domClass(table, id)

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
		def aa = jsshDbStorageService.domClass('update', table, id, params)
		def uiterator = aa.uiterator
		def result = aa.result

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

		if (!result) {
			render(template: "/jsshadmin/edit",  model: model)
			return
		}

		flash.message = message(code: 'default.updated.message', args: [message(code: table+'.label', default: table), uiterator.id])
		render(template: "/jsshadmin/edit",  model: model)
	}


	def delete(Long id, String table) {
		
		def aa = jsshDbStorageService.domClass('delete', table, id, params)
		def uiterator = aa.uiterator
		def result = aa.result

		Map model = [table:table, lookup:table, uiterator:uiterator, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]
		if (!uiterator) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: table+'.label', default: table), id])
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
			return
		}
		if (result) {
			flash.message = message(code: 'default.deleted.message', args: [message(code: table+'.label', default: table), id])
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
		}else{
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: table+'.label', default: table), id])
			redirect(controller: "connectSsh", action: "siteAdmin", params: model)
		}
	}

	def viewLogs(String id, String table) {
		
		JsshUser juser = JsshUser.get(id)
		String icom = params.comlog
		def resultSet, resultCount
		params.max = Math.min(params.int('max') ?: 50, 100)
		int total = 0
		String order = params.order ?: "desc"
		String sortby = params.sortby ?: "dateCreated"
		int offset = (params.offset ?: '0') as int
		def paginationParams = [sort: sortby, order: order, offset: offset, max: params.max]
		if (juser && table == "commands") {
			if (icom) {
				def clog = CommandLogger.get(icom)
				resultSet = CommandLogs.findAllByConlogAndComlog(juser.conlog,clog, paginationParams)
				resultCount = CommandLogs.countByConlogAndComlog(juser.conlog, clog)
			}else{
				resultSet = CommandLogs.findAllByConlog(juser.conlog, paginationParams)
				resultCount = CommandLogs.countByConlog(juser.conlog)
			}
		}else if (juser && table == "connections") {
			resultSet = ConnectionLogs.findAllByConlog(juser.conlog, paginationParams)
			resultCount =  ConnectionLogs.countByConlog(juser.conlog)
		}
		verifyBooleans(params)
		Map model = [ loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle,
			resultSet:resultSet, resultCount:resultCount, table:table, id:id]

		render template: '/jsshadmin/view_'+table, model : model
	}

	private verifyBooleans(params){
		this.loadBootStrap = (params.loadBootStrap as Boolean) ?: true
		this.loadJQuery = (params.loadJQuery as Boolean) ?: true
		this.loadStyle = (params.loadStyle as Boolean) ?: true
	}

}
