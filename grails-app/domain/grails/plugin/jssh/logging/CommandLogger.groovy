package grails.plugin.jssh.logging

class CommandLogger {
	
		Date dateCreated
		Date lastUpdated
	  
		static hasMany = [ commands: CommandLogs]
	  
		static constraints = {
		}
	  
		static mapping = {
		  commands cascade: 'all-delete-orphan'
		}
	  
		String getFormattedDateCreated() {
		  dateCreated.format("MM/dd/yyyy 'at' h:mma")
		}
	  }