package grails.plugin.jssh

import grails.plugin.jssh.logging.CommandLogger
import grails.plugin.jssh.logging.ConnectionLogger
import grails.plugin.jssh.logging.ConnectionLogs
import grails.transaction.Transactional


@Transactional
class DBStorageService extends ConfService{

	public JsshUser addJsshUser(String username, String serverId, String groupId=null) {
		JsshUser user
		JsshUser.withTransaction {
			user = JsshUser.findByUsername(username)
			if (!user) {
				ConnectionLogs addlog = addLog()
				SshServers ss = SshServers.get(serverId)
				SshServerGroups ssg
				if (groupId) {
					ssg = SshServerGroups.get(groupId)
				}
				user = new JsshUser(username:username, conlog: addlog, servers: ss, groups: ssg)
				if (!user.save(flush:true)) {
					if (config.debug == "on") {
						user.errors.allErrors.each{println it}
					}
				}
			}
		}
		return user
	}
	
	public SshServers addServer(String hostName, String ip=null, String port=null) {
		SshServers server
		SshServers.withTransaction {
			server = SshServers.findByHostName(hostName)
			if (!server) {
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
	
	private CommandLogger addComLog(String conId) {
		CommandLogger.withTransaction {
			ConnectionLogs con = ConnectionLogs.get(conId)
			CommandLogger logInstance = new CommandLogger(commandlogs: [connections: con])
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
			ConnectionLogger logInstance = new ConnectionLogger(connections: [])
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
			return logInstance
		}
	}
}
