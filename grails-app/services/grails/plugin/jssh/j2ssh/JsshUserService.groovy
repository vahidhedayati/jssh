package grails.plugin.jssh.j2ssh

import grails.plugin.jssh.JsshUser

class JsshUserService {

	public Boolean isAdmin(String jsshUser) {
		boolean admin = false
		JsshUser juser = JsshUser.findByUsername(jsshUser)
		if (juser) {
			String perm = juser.permissions
			if (perm.toLowerCase().startsWith('admin')) {
				admin = true
			}
		}
		return admin
	}
}
