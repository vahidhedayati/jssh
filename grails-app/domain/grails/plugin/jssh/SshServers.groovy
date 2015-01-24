package grails.plugin.jssh

class SshServers {
	
	static belongsTo = [JsshUser, SshServerGroups]
	
	String hostName
	String ipAddress
	String sshPort
	
    static constraints = {
		hostName blank: false, unique: true
		ipAddress nullable:true
		sshPort nullable:true
    }
}
