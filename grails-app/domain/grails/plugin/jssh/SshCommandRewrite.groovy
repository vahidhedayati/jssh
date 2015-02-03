package grails.plugin.jssh


class SshCommandRewrite {
	
	SshUser sshuser
	
	String command
	String replacement
	
	static constraints = {
		command blank: false, unique: true
	}
}
