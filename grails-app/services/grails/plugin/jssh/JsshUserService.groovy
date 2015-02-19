package grails.plugin.jssh

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
