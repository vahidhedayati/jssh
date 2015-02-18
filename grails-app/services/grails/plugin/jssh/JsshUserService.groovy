package grails.plugin.jssh

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

import com.sshtools.j2ssh.SshClient
import com.sshtools.j2ssh.configuration.SshConnectionProperties
import com.sshtools.j2ssh.session.SessionChannelClient

/*
 * Vahid Hedayati 
 * AuthService was originally set up as a way of determining what type of socket connection
 *  it was and then defining which jssh service to connect to 
 *  client/server changed to push info back to processService to do connection
 *  have a look for multiUser in processService which used to be all in here.
 *   
 */
class JsshUserService {

	public Boolean isAdmin(String jsshUser) {
		boolean admin = false
		JsshUser juser = JsshUser.findByUsername(jsshUser)
		String perm = juser.permissions
		if (perm.toLowerCase().startsWith('admin')) {
			admin = true
		}
		return admin
	}
}
