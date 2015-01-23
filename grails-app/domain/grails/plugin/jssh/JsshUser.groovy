package grails.plugin.jssh

import grails.plugin.jssh.logging.ConnectionLogger

class JsshUser {

	ConnectionLogger conlog
	
	String username
	
	static hasMany = [sshuser: SshUser, servers: SshServers, groups: SshServerGroups]
	
    static constraints = {
		username blank: false, unique: true
		conlog nullable: true
		//comlog nullable: true
    }
	
	static mapping = { 
		servers cascade: 'lock'
		sshuser cascade: 'lock'
	}
}
