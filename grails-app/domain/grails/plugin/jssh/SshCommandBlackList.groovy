package grails.plugin.jssh


class SshCommandBlackList {
	
	SshUser sshuser
	
	String command
	
	static constraints = {
		command blank: false
		//, unique: true
	}
	
	//String toString() {
	//	"${command}"
	//}
}
