package grails.plugin.jssh

class SshServerGroups {
	
	JsshUser user
	
	String name
	
	static hasMany = [ servers: SshServers]
	
    static constraints = {
		name blank: false, unique: true
    }
}
