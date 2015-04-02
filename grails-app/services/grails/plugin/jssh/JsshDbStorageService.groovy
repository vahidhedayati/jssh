package grails.plugin.jssh

import grails.plugin.jssh.logging.CommandLogger
import grails.plugin.jssh.logging.CommandLogs
import grails.plugin.jssh.logging.ConnectionLogger
import grails.plugin.jssh.logging.ConnectionLogs
import grails.transaction.Transactional

import org.springframework.dao.DataIntegrityViolationException



class JsshDbStorageService extends JsshConfService {

	def jsshDnsService

	public SshServers  addServer(String username, String hostName,  String port, String ip, String groupId) {
		SshServers server = addServer(hostName,   port,  ip)
		addGroupLink(groupId)
		return server
	}

	@Transactional
	public Map domClass(String atype, String table, Long id, params=null) {
		boolean result = true
		def uiterator = domClass(table, id)

		if (atype == 'update') {
			uiterator.properties = params
			if (!uiterator.save(flush: true)) {
				result = false
			}
		}else if (atype == 'delete') {
			try {
				//if (table == "jsshUser") {
				//	uiterator = JsshUser.get(id)
				//	uiterator.sshuser.clear()
				//	uiterator.sshuser.each { suser ->
				//		uiterator.removeFromSShUser(suser)
				//		uiterator.delete()
				////	}
					
				//}else{	
					uiterator.delete(flush: true)
				//}
			}catch (DataIntegrityViolationException e) {
				result = false
			}
		}
		return [result:result, uiterator:uiterator]
	}


	@Transactional
	def domClass(String table, Long id) {
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
		return uiterator
	}

	@Transactional
	public Map siteAdmin(String lookup, params) {

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

		return [userInstanceList:userInstanceList, userInstanceTotal:userInstanceTotal]
	}
	@Transactional
	public Map findServer(String hostName) {
		def returnResult=[:]
		def found = SshServers.findByHostName(hostName)
		if (found) {
			returnResult=[status: 'found', id: found.id as String]
		}else{
			returnResult=[status: 'not_found']
		}
		return returnResult
	}

	@Transactional
	public Map findSshUser(String username) {
		def returnResult=[:]
		def found = SshUser.findByUsername(username)
		if (found) {
			returnResult=[status: 'found', id: found.id as String]
		}else{
			returnResult=[status: 'not_found']
		}
		return returnResult
	}

	@Transactional
	public Map findSshCommand(String type, String command) {
		def returnResult=[:]
		def found
		if (type=="blacklist") {
			found = SshCommandBlackList.findByCommand(command)
		}

		if (type=="rewrite") {
			found = SshCommandRewrite.findByCommand(command)
		}

		if (found) {
			returnResult=[status: 'found', id: found.id as String]
		}else{
			returnResult=[status: 'not_found']
		}
		return returnResult
	}

	@Transactional
	public Map autoBlackList(String username, String cmd, String sshId) {
		def suser = SshUser.get(sshId)
		SshCommandBlackList found = SshCommandBlackList.findBySshuserAndCommand(suser, cmd)
		if (!found) {
			found = new SshCommandBlackList(sshuser: suser, command: cmd)
			if (!found.save(flush:true)) {
				if (debug) {
					found.errors.allErrors.each{log.error it}
				}
			}
			return [record_added: found.id]
		}
	}

	@Transactional
	public SshServerGroups storeGroup(String name, String username) {
		username = parseFrontEnd(username)
		JsshUser user = JsshUser.findByUsername(username)
		SshServerGroups sg
		SshServerGroups.withTransaction {
			sg = SshServerGroups.findOrSaveByNameAndUser(name, user)
			if (!sg.save(flush:true)) {
				if (debug) {
					sg.errors.allErrors.each{log.error it}
				}
			}
		}
		if (user && sg) {
			user.addToGroups(sg)
			if (!user.save(flush:true)) {
				if (debug) {
					user.errors.allErrors.each{log.error it}
				}
			}
			//return "\n\nGroup ${sg.name } should now be added for ${user.username}"
			return sg
		}
	}

	@Transactional
	public String storeConnection(String hostName, String port, String user,  String sshUser, String conId = null) {
		ConnectionLogs logInstance
		//ConnectionLogs.withTransaction {
		ConnectionLogger conlog

		if (conId) {

			conlog = ConnectionLogger.get(conId)
		}else{

			conlog = addLog()
		}

		logInstance = new ConnectionLogs(jsshUser: user, sshUser: sshUser, hostName: hostName, port: port ?: '22', conlog: conlog)
		if (!logInstance.save(flush:true)) {
			if (debug) {
				logInstance.errors.allErrors.each{log.error it}
			}
		}
		//}
		return logInstance.id as String
	}
	@Transactional
	public Map<String,String> addJsshUser(String username, SshServers server, String permissions=null, String groupId=null) {
		JsshUser user
		JsshPermissions perm
		if (!permissions) {
			permissions = config.defaultperm  ?: 'user'
		}
		//JsshPermissions.withTransaction {
		perm = JsshPermissions.findByName(permissions)
		if (!perm) {
			perm = JsshPermissions.findOrSaveWhere(name: permissions).save(flush:true)
		}
		//}
		//JsshUser.withTransaction {
		user = JsshUser.findByUsername(username)
		if (!user) {
			ConnectionLogger addlog = addLog()
			SshServerGroups ssg
			if (groupId) {
				ssg = SshServerGroups.get(groupId)
			}

			user = new JsshUser(username:username, permissions: perm, conlog: addlog, servers: server, groups: ssg)
			if (!user.save(flush:true)) {
				if (debug) {
					user.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
		return [ user: user.id as String, conId: user.conlog.id as String ]
	}

	@Transactional
	public String addRewriteEntry(String username, String sshUserId, String command, String replacement) {
		//SshCommandRewrite.withTransaction {
		SshUser suser = SshUser.get(sshUserId)
		if (suser) {
			SshCommandRewrite found = SshCommandRewrite.findBySshuserAndCommand(suser, command)
			if (!found) {
				found = new SshCommandRewrite(sshuser: suser, command: command, replacement: replacement )
				if (!found.save(flush:true)) {
					if (debug) {
						found.errors.allErrors.each{log.error it}
					}
				}
				return "Record Added: $found.id"
			}
			return "Already exists: $found.id"
		}
		//}
		return "${sshUserId} not found "
	}

	@Transactional
	public SshServers addServer(String username, String hostName,  String port, String ip) {
		SshServers server
		JsshUser user
		//SshServers.withTransaction {
		user = JsshUser.findByUsername(username)
		server = SshServers.findByHostName(hostName)
		if (!server) {

			if (!port) {
				port = "22"
			}

			if (!ip) {
				Map i = jsshDnsService.hostLookup(hostName)
				ip = i.ip ?: i.fqdn ?: i.name ?:  '127.0.0.1'
			}

			server = new SshServers(hostName: hostName, ipAddress: ip, sshPort:port)
			if (!server.save(flush:true)) {
				if (debug) {
					server.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
		if (user) {
			user.addToServers(server)
			if (!user.save(flush:true)) {
				if (debug) {
					user.errors.allErrors.each{log.error it}
				}
			}
		}
		return server
	}

	@Transactional
	def addGroupServers(String groupId, ArrayList serverList) {
		serverList.each { sn ->
			SshServers server = SshServers.findByHostName(sn)
			addGroupLink(groupId, server)
		}
	}

	@Transactional
	public SshServerGroups addGroup(String name, String serverId) {
		SshServerGroups sg
		//SshServerGroups.withTransaction {
		sg = SshServerGroups.findByName(name)
		if (!sg) {
			SshServers ss = SshServers.get(serverId)
			sg = new SshServerGroups(name:name, servers: ss)

			if (!sg.save(flush:true)) {
				if (debug) {
					sg.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
		return sg
	}

	public void activeUsers() {
		if (config.logusers == "on") {
			StringBuilder sb= new StringBuilder("Size: "+sshUsers.size()+"\n")
			synchronized (sshUsers) {
				sshUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						def cjob = crec.userProperties.get("job").toString()
						sb.append("${cuser}:${cjob}\n")
					}
				}
			}
			log.error sb.toString()
		}
	}

	@Transactional
	private SshUser addSshUser(String friendlyName, String username, String sshUsername, String sshKey=null, String sshKeyPass=null) {
		JsshUser user = JsshUser.findByUsername(username)
		SshUser suser = SshUser.findByUsername(sshUsername)
		if (!suser) {
			//SshUser.withTransaction {
			suser = SshUser.findOrSaveWhere(friendlyName: friendlyName, username: sshUsername, sshKey: sshKey, sshKeyPass: sshKeyPass)
			if (!suser.save(flush:true)) {
				if (debug) {
					suser.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
		if (user) {
			user.addToSshuser(suser)
		}
		return suser
	}

	@Transactional
	public addSShUser(String friendlyName, String username, String sshUsername, String sshKey=null, String sshKeyPass=null,  ArrayList serverId) {
		SshUser suser=addSshUser(friendlyName, username, sshUsername, sshKey, sshKeyPass)
		if (serverId) {
			serverId.each { ss ->
				SshServers server = SshServers.get(ss)
				suser.addToServers(server)
				server.sshuser = suser
				if (!server.save(flush:true)) {
					if (debug) {
						server.errors.allErrors.each{log.error it}
					}
				}
			}
		}
	}

	// This will attempt to clone jsshUser and all its bindings
	// bindings being:
	// permissions
	// ssh user(s)  its keys + server bindings + command blacklist + command rewrites
	// server(s)  + all server
	// groups (s)
	@Transactional
	public String cloneUser(username, masteruser) {
		StringBuilder msg = new StringBuilder()
		def record=JsshUser.findByUsername(masteruser)
		def found=JsshUser.findByUsername(username)
		if (!found) {
			def newRecord = new JsshUser()
			copyProperties(record, newRecord)

			newRecord.username=username

			ConnectionLogger addlog = addLog()
			newRecord.conlog = addLog()

			if (! newRecord.save(flush: true)) {
				newRecord.errors.allErrors.each{log.error it}
				return
			}
			deeperCopy(record,newRecord, username)

		}else{
			msg.append("${username} already exists!")
		}
		return msg.toString()
	}

	@Transactional
	def deeperCopy(source, target, String username) {
		def (sProps, tProps) = [source, target]*.properties*.keySet()
		def commonProps = sProps.intersect(tProps) - ['class', 'metaClass']
		def hMany = JsshUser."hasMany"
		commonProps.each { cp ->
			if (!cp.endsWith('Id')) {
				if (cp in hMany) {
					if (cp=="servers") {
						source[cp].each { SshServers cr->
							addServer(username, cr.hostName,  cr.sshPort, cr.ipAddress)
						}
					}else if (cp=="groups") {
						source[cp].each { SshServerGroups cr->
							SshServerGroups sg = storeGroup("${cr.name}", username)
							cr.servers.each {  SshServers ss->
								addGroupLink(sg.id as String, ss)
							}
						}
					} else if (cp=="sshuser") {
						source[cp].each { SshUser cr->
							addSShUser(cr.friendlyName, username, cr.username, cr.sshKey, cr.sshKeyPass)
							cr.rewrite.each { SshCommandRewrite rw ->
								addRewriteEntry(username, cr.id as String, rw.command, rw.replacement)
							}
							cr.blacklist.each { SshCommandBlackList bl ->
								autoBlackList(username, bl.command, cr.id as String)
							}
							//cr.servers.each {}
						}
					}
				}
			}
		}
	}

	def copyProperties(source, target) {
		def (sProps, tProps) = [source, target]*.properties*.keySet()
		def commonProps = sProps.intersect(tProps) - ['class', 'metaClass']
		def hMany = JsshUser."hasMany"
		commonProps.each { cp ->
			if (!cp.endsWith('Id')) {
				if (cp in hMany) {
				}else{
					target[cp] = source[cp]
				}
			}
		}
	}

	private String currentController(String s) {
		s.substring(0,1).toUpperCase() + s.substring(1)
	}

	@Transactional
	private void addGroupLink(String groupId, SshServers server) {
		//SshServerGroups.withTransaction {
		def d1 = SshServerGroups.get(groupId)
		if (d1) {
			d1.addToServers(server)
			if (!d1.save(flush:true)) {
				if (debug) {
					d1.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
	}

	@Transactional
	private CommandLogger addComLog() {
		//CommandLogger.withTransaction {
		//ConnectionLogs con = ConnectionLogs.get(conId)
		CommandLogger logInstance = new CommandLogger(commands: [])
		if (!logInstance.save(flush:true)) {
			if (debug) {
				logInstance.errors.allErrors.each{log.error it}
			}
		}
		return logInstance
		//}
	}

	@Transactional
	private ConnectionLogger addLog() {
		//ConnectionLogger.withTransaction {
		ConnectionLogger logInstance = new ConnectionLogger(connections: [], commands: [])
		if (!logInstance.save(flush:true)) {
			if (debug) {
				logInstance.errors.allErrors.each{log.error it}
			}
		}
		return logInstance
		//}
	}

	@Transactional
	private String storeCommand(String message, String hostName, String user, String conId, String sshUser, String comId = null) {
		CommandLogs logInstance
		//CommandLogs.withTransaction {
		CommandLogger comlog
		if (conId) {
			ConnectionLogger con = ConnectionLogger.get(conId as Long)
			JsshUser ju = JsshUser.findByUsername(user)
			if (comId) {
				comlog = CommandLogger.get(comId as Long)
				if (!comlog) {
					comlog = addComLog()
				}
			}else{
				comlog = addComLog()
			}
			logInstance = new CommandLogs(user: user, sshUser: sshUser, hostName: hostName, contents: message, comlog: comlog, conlog: con ?: ju.conlog)
			if (!logInstance.save(flush:true)) {
				if (debug) {
					logInstance.errors.allErrors.each{log.error it}
				}
			}
		}
		//}
		return logInstance.id as String
	}

}