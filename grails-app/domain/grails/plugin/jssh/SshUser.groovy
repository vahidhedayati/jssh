package grails.plugin.jssh


class SshUser {
	
	static belongsTo = [JsshUser]
	
	String username
	String sshKey
	
    static constraints = {
		username blank: false, unique: true
		sshKey  nullable:true
	}
	
	
}
