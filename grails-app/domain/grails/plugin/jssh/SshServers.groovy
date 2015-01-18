package grails.plugin.jssh

class SshServers {
	
	SshServerGroups mygroup
	
	static belongsTo = [JsshUser]
	
	String hostName
	String ipAddress
	String sshPort
	
	
    static constraints = {
		mygroup nullable:true
		
		ipAddress nullable:true
		sshPort nullable:true
		hostName blank: false
    }
}
