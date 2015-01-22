package grails.plugin.jssh

import javax.websocket.Session



class ConfService {

	static transactional  =  false
	
	def grailsApplication
	
	static final Set<Session> sshUsers = ([] as Set).asSynchronized()
	
	public String CONNECTOR = "CONN:-"
	public String DISCONNECTOR = "DISCO:-"
	public String APP = "j2ssh"
	public String VIEW = "connectSsh"


	public boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}

	public String getFrontend() {
		return config.frontenduser ?: '_frontend'
	}

	public String getAppName(){
		String addAppName = config.add.appName ?: 'yes'
		if (addAppName) {
			grailsApplication.metadata['app.name']
		}else{
			return
		}
	}

	
	public Map<String, String> parseInput(String mtype,String message){
		def p1 = mtype
		def mu = message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset = []
		if (mu.indexOf(",")>-1) {
			user = mu.substring(0,mu.indexOf(","))
			msg = mu.substring(user.length()+1,mu.length())
		}else{
			user = mu.substring(0,mu.indexOf(" "))
			msg = mu.substring(user.length()+1,mu.length())
		}
		Map<String, String> values  =  new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}

	def getConfig() {
		grailsApplication?.config?.jssh
	}
	public String parseFrontEnd(String username) {
		if (username.endsWith(frontend)) {
			username=username.substring(0, username.indexOf(frontend))
		}
		return username
	}

}
