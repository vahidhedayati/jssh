package grails.plugin.jssh

class SshServerGroups {
	
	JsshUser user
	
	String name
	
	static hasMany = [ servers: SshServers]
	
	
    static constraints = {
		name blank: false
    }
	
	static mapping = { 
		servers cascade: 'lock'
	}
	
	String toString() {
		"${name}"
	}
}
