package grails.plugin.jssh.logging

class CommandLogs {

	CommandLogs comlog
	
	static hasMany = [connections: ConnectionLogs]
	
	String user
	String contents
	Date dateCreated
  
	static constraints = {
	  user nullable: true
	}
  
	static mapping = {
	  contents type: 'text'
	}
  
	String toString() {
	  if (user) {
		return "$user: $contents"
	  }
	  return contents
	}
}