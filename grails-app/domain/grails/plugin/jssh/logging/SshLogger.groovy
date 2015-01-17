package grails.plugin.jssh.logging

class SshLogger {
	
		Date dateCreated
		Date lastUpdated
	  
		static hasMany = [connections: ConnectionLogs, commands: CommandLogs]
	  
		static constraints = {
		}
	  
		static mapping = {
		  connections cascade: 'all-delete-orphan'
		  commands cascade: 'all-delete-orphan'
		}
	  
		String getFormattedDateCreated() {
		  dateCreated.format("MM/dd/yyyy 'at' h:mma")
		}
	  }