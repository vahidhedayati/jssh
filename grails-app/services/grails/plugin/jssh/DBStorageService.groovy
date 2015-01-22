package grails.plugin.jssh

import grails.plugin.jssh.logging.ConnectionLogs
import grails.transaction.Transactional


@Transactional
class DBStorageService extends ConfService{

	def addJsshUser(String username) {
		//def cuser = currentUser(username)
		JsshUser.withTransaction {
			def user = JsshUser.findByUsername(username)
			if (!user) {
				def addlog = addLog()
				user = JsshUser.findOrSaveWhere(username:username, conlog: addlog).save(flush:true)
			}
		}
	}
	
	private ConnectionLogs addLog() {
		ConnectionLogs.withTransaction {
			ConnectionLogs logInstance = new ConnectionLogs(messages: [])
			if (!logInstance.save(flush:true)) {
				if (config.debug == "on") {
					logInstance.errors.allErrors.each{println it}
				}
			}
			return logInstance
		}
	}
}
