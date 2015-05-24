package grails.plugin.jssh.j2ssh

import grails.plugin.jssh.interfaces.JsshBase

import java.util.concurrent.ConcurrentMap

import javax.websocket.Session



class JsshConfService implements JsshBase {

	static transactional  =  false

	def grailsApplication

	public ConcurrentMap<String, Session> getSshNames() {
		return sshUsers
	}

	public Collection<String> getJsshUsers() {
		return Collections.unmodifiableSet(sshUsers.keySet())
	}

	public Session getSshUser(String username) {
		Session userSession = sshUsers.get(username)
		return userSession
	}

	public boolean sshUserExists(String username) {
		return jsshUsers.contains(username)
	}

	public boolean destroySshUser(String username) {
		if (sshUserExists(username)) {
			return sshUsers.remove(username) != null
		}
	}

	public String CONNECTOR = "CONN:-"
	public String DISCONNECTOR = "DISCO:-"
	public String APP = "j2ssh"
	public String VIEW = "connectSsh"

	public boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}

	public String getFrontend() {
		return config?.frontenduser ?: '_frontend'
	}

	public Boolean getDebug () {
		boolean on = false
		if (config?.debug == "on") {
			on = true
		}
		return on
	}

	public String getAppName(){
		String addAppName = config.add.appName ?: 'yes'
		if (addAppName=='yes') {
			grailsApplication.metadata['app.name']
		}else{
			return ''
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

	public ConfigObject getConfig() {
		return grailsApplication.config.jssh ?: ''
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
