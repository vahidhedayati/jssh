package grails.plugin.jssh

import java.util.Map;

import grails.plugin.jssh.logging.CommandLogger
import grails.plugin.jssh.logging.CommandLogs
import grails.plugin.jssh.logging.ConnectionLogger
import grails.plugin.jssh.logging.ConnectionLogs
import grails.transaction.Transactional


@Transactional
class DbStorageService extends ConfService {

	def dnsService

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

	//public String storeServers(ArrayList servers, String gpId, String username) {}

	public String storeGroup(String name, String username) {
		SshServerGroups.withTransaction {
			username = parseFrontEnd(username)
			JsshUser user = JsshUser.findByUsername(username)
			SshServerGroups sg = SshServerGroups.findOrSaveByNameAndUser(name, user)
			if (!sg.save(flush:true)) {
				if (config.debug == "on") {
					sg.errors.allErrors.each{println it}
				}
			}
			return "\n\nGroup ${sg.name } should now be added for ${user.username}"
		}
	}

	public String storeConnection(String hostName, String port, String user,  String sshUser, String conId = null) {
		ConnectionLogs logInstance
		ConnectionLogs.withTransaction {
			ConnectionLogger conlog
			if (conId) {
				conlog = ConnectionLogger.get(conId)
			}else{
				conlog = addLog()
			}
			logInstance = new ConnectionLogs(jsshUser: user, sshUser: sshUser, hostName: hostName, port: port, conlog: conlog)
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
		}
		return logInstance.id as String
	}

	public Map<String,String> addJsshUser(String username, SshServers server, String groupId=null) {
		JsshUser user
		JsshUser.withTransaction {
			user = JsshUser.findByUsername(username)
			if (!user) {
				ConnectionLogger addlog = addLog()
				SshServerGroups ssg
				if (groupId) {
					ssg = SshServerGroups.get(groupId)
				}
				user = new JsshUser(username:username, conlog: addlog, servers: server, groups: ssg)
				if (!user.save(flush:true)) {
					if (config.debug == "on") {
						user.errors.allErrors.each{println it}
					}
				}
			}
		}
		return [ user: user.id as String, conId: user.conlog.id as String ]
	}

	public SshServers  addServer(String username, String hostName,  String port, String ip, String groupId) {
		SshServers server = addServer(hostName,   port,  ip)
		addGroupLink(groupId)
		return server
	}

	public SshServers addServer(String username, String hostName,  String port, String ip) {
		SshServers server
		JsshUser user
		SshServers.withTransaction {
			user = JsshUser.findByUsername(username)
			server = SshServers.findByHostName(hostName)
			if (!server) {

				if (!port) {
					port = "22"
				}

				if (!ip) {
					Map i = dnsService.hostLookup(hostName)
					ip = i.ip ?: i.fqdn ?: i.name ?:  '127.0.0.1'
				}

				server = new SshServers(hostName: hostName, ipAddress: ip, sshPort:port)
				if (!server.save(flush:true)) {
					if (config.debug == "on") {
						server.errors.allErrors.each{println it}
					}
				}
			}
		}
		if (user) {
			user.addToServers(server)
			if (!user.save(flush:true)) {
				if (config.debug == "on") {
					user.errors.allErrors.each{println it}
				}
			}
		}
		return server
	}

	def addGroupServers(String groupId, ArrayList serverList) {
		serverList.each { sn ->
			SshServers server = SshServers.findByHostName(sn)
			addGroupLink(groupId, server)
		}

	}

	/*
	 def addGroupServers(String groupId, String serverList) {
	 println ">>>::: 2 ------- > ${sn} :: ${serverList}"
	 SshServers server = SshServers.findByHostName(serverList)
	 addGroupLink(groupId, server)
	 }
	 */

	public SshServerGroups addGroup(String name, String serverId) {
		SshServerGroups sg
		SshServerGroups.withTransaction {
			sg = SshServerGroups.findByName(name)
			if (!sg) {
				SshServers ss = SshServers.get(serverId)
				sg = new SshServerGroups(name:name, servers: ss)

				if (!sg.save(flush:true)) {
					if (config.debug == "on") {
						sg.errors.allErrors.each{println it}
					}
				}
			}
		}
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
			log.info sb.toString()
		}
	}

	private void addGroupLink(String groupId, SshServers server) {
		SshServerGroups.withTransaction {
			def d1 = SshServerGroups.get(groupId)
			if (d1) {
				d1.addToServers(server)
				if (!d1.save(flush:true)) {
					if (config.debug == "on") {
						d1.errors.allErrors.each{println it}
					}
				}
			}
		}

	}
	private CommandLogger addComLog() {
		CommandLogger.withTransaction {
			//ConnectionLogs con = ConnectionLogs.get(conId)
			CommandLogger logInstance = new CommandLogger(commands: [])
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
			return logInstance
		}
	}

	private ConnectionLogger addLog() {
		ConnectionLogger.withTransaction {
			ConnectionLogger logInstance = new ConnectionLogger(connections: [], commands: [])
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
			return logInstance
		}
	}

	private String storeCommand(String message, String user, String conId, String sshUser, String comId = null) {
		CommandLogs logInstance
		CommandLogs.withTransaction {
			CommandLogger comlog
			ConnectionLogger con = ConnectionLogger.get(conId)
			if (comId) {
				comlog = CommandLogger.get(comId)
			}else{
				comlog = addComLog()
			}
			logInstance = new CommandLogs(user: user, sshUser: sshUser, contents: message, comlog: comlog, conlog: con)
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
		}
		return logInstance.id as String
	}

}