package grails.plugin.jssh

import javax.websocket.Session



class JsshConfService {

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
	
	public String parseBash(String input) {

		Map bashMap = [
			'[1;30m' : '<span style="color:black">',
			'[1;31m' : '<span style="color:red">',
			'[1;32m' : '<span style="color:green">',
			'[1;33m' : '<span style="color:yellow">',
			'[1;34m' : '<span style="color:blue">',
			'[1;35m' : '<span style="color:purple">',
			'[1;36m' : '<span style="color:cyan">',
			'[1;37m' : '<span style="color:white">',
			'[m'   : '</span>',
			'[0m'   : '</span>'
		]

		bashMap.each { k,v ->
			if (input.contains(k)) {
				//input = input.toString().replace(k, v)
				input = input.replace(k,'')
			}
		}
		return input
	}

	def getConfig() {
		grailsApplication?.config?.jssh
	}
	
	public String addFrontEnd(String username) {
		if (!username.endsWith(frontend)) {
			username=username+frontend
		}
		return username
	}
	
	public String parseFrontEnd(String username) {
		if (username && username.endsWith(frontend)) {
			username=username.substring(0, username.indexOf(frontend))
		}
		return username
	}

}