package grails.plugin.jssh


class SshUser {
	
	static hasMany = [servers: SshServers, blacklist: SshCommandBlackList, rewrite: SshCommandRewrite]
	
	static belongsTo = [JsshUser]
	
	String friendlyName
	String username
	String sshKey
	String sshKeyPass
	
    static constraints = {
		username blank: false
		sshKey  nullable:true
		sshKeyPass  nullable:true
		friendlyName nullable:true
	}
	
	static mapping = {
		servers cascade: 'lock'
	}
	
	String toString(){ 
		"${friendlyName} : ${username}"
	}
}
