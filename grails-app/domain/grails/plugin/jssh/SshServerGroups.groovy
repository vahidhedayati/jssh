package grails.plugin.jssh

class SshServerGroups {
	
	SshUser user
	
	String name
	
	static hasMany = [ servers: SshServers]
	
    static constraints = {
		name blank: false
    }
}
