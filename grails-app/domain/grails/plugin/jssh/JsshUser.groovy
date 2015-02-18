package grails.plugin.jssh

import grails.plugin.jssh.logging.ConnectionLogger

class JsshUser {

	ConnectionLogger conlog
	JsshPermissions permissions
	String username
	
	static hasMany = [sshuser: SshUser, servers: SshServers, groups: SshServerGroups]
	
    static constraints = {
		username blank: false, unique: true
		conlog nullable: true
    }
	
	static mapping = { 
		permissions lazy: false
		servers cascade: 'lock'
		sshuser cascade: 'lock'
	}
	
	String toString() {
		"${username}"
	}
}
