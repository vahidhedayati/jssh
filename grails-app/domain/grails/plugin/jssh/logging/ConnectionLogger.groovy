package grails.plugin.jssh.logging

class ConnectionLogger {
	
		Date dateCreated
		Date lastUpdated
	  
		static hasMany = [connections: ConnectionLogs, commands: CommandLogs]
	  
		static constraints = {
			commands nullable: true
		}
	  
		static mapping = {
		  connections cascade: 'all-delete-orphan'
		}
	  
		String getFormattedDateCreated() {
		  dateCreated.format("MM/dd/yyyy 'at' h:mma")
		}
		
		String toString() {
			"${id}"
		}
	  }