package grails.plugin.jssh

import grails.plugin.jssh.logging.CommandLogger
import grails.plugin.jssh.logging.CommandLogs
import grails.plugin.jssh.logging.ConnectionLogger
import grails.plugin.jssh.logging.ConnectionLogs
import grails.transaction.Transactional


@Transactional
class DbStorageService extends ConfService {

	def dnsService
	
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
	
	public SshServers  addServer(String hostName,  String port) {
		SshServers server
		SshServers.withTransaction {
			server = SshServers.findByHostName(hostName)
			if (!server) {
				Map i = dnsService.hostLookup(hostName)
				String ip = i.ip ?: i.name ?: i.fqdn ?: 'loocalhoost'
				server = new SshServers(hostName: hostName, ipAddress: ip, sshPort:port)
				if (!server.save(flush:true)) {
					if (config.debug == "on") {
						server.errors.allErrors.each{println it}
					}
				}
			}
		}
		return server
	}
	
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