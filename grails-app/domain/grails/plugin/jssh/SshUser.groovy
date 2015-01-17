package grails.plugin.jssh

import grails.plugin.jssh.logging.SshLogger

class SshUser {

	String username
	String password
	String sshKey
	
	SshLogger log
	
	static hasMany = [servers: SshServers, groups: SshServerGroups]
	
    static constraints = {
		username blank: false, unique: true
		password  nullable:true
		sshKey  nullable:true
		log nullable: true
    }
	
	static mapping = { 
		servers cascade: 'lock'
		//groups cascade: 'lock'
	}
}
