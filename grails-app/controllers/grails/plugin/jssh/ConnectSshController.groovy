package grails.plugin.jssh

class ConnectSshController extends ConfService {
	def connectSsh
	def randService
	//def jsshConfig
	private Map menuMap = ['index':'Socket method', 'ajaxpoll':'Ajax poll', 
		'socketremote':'RemoteForm Websocket', 'scsocket':'NEW: Websocket Client/Server']
	
	def index() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "index disabled"
		}
		def hideAuthBlock = config.hideAuthBlock
		[hideAuthBlock:hideAuthBlock, process:process, menuMap:menuMap]
	}
	
	
	def socketremote() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "socketremote disabled"
		}
		[process:process, menuMap:menuMap]
	}

	def ajaxpoll() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
	}

	
	def scsocket() {
		def process = config.disable.login ?: 'NO'
		if (process.toString().toLowerCase().equals('yes')) {
			render "ajaxpoll disabled"
		}
		[process:process, menuMap:menuMap]
	}


	def process	(String jsshUser, String hostname, String username, String userCommand,
		String port, String password, String divId, String sshTitle, String view) {
		if (!divId) {
			divId = randService.shortRand('divId')
		}
		if (!sshTitle) {
			sshTitle = "Grails Jssh plugin: ${view}"
		}
		
		Map map =  [jsshUser: jsshUser, hostname: hostname, username: username, userCommand:userCommand,
			password: password, divId: divId, sshTitle: sshTitle, menuMap:menuMap]
		
		render view:view, model: map
	}

	def resetOutput() { 
		connectSsh.setOutput(new StringBuilder())
		render ''
	}

	def closeConnection() {
		connectSsh?.closeConnection()
		log.info "Connection closed"
		redirect(url: session.referrer ?: '/')
	}

	def inspection() {
		def input = connectSsh.output
		render "${input}"
	}

	def inspect() {
		def input = connectSsh.output
		[input:input ]
	}
}
