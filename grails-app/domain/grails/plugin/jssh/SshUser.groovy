package grails.plugin.jssh


class SshUser {
	
	static hasMany = [servers: SshServers]
	
	static belongsTo = [JsshUser]
	
	String username
	String sshKey
	String sshKeyPass
    static constraints = {
		username blank: false, unique: true
		sshKey  nullable:true
		sshKeyPass  nullable:true
	}
	
	static mapping = {
		servers cascade: 'lock'
	}
	
	
}
