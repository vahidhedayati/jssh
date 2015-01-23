package grails.plugin.jssh.logging

class CommandLogs {

	CommandLogger comlog
	ConnectionLogger conlog
	
	String user
	String sshUser
	
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