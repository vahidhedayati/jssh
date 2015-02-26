package grails.plugin.jssh.logging

class ConnectionLogs {

	ConnectionLogger conlog
	String jsshUser
	String sshUser
	String hostName
	String port
	
	Date dateCreated
  
	static constraints = {
	  jsshUser nullable: true
	  port nullable: true
	}
  
	String toString() {
	  if (jsshUser) {
		return "$jsshUser: $sshUser: $hostName"
	  }
	  return "$sshUser: $hostName"
	}
}