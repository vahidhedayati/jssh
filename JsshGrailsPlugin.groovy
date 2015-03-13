import grails.plugin.jssh.ConnectSsh
import grails.plugin.jssh.J2sshEndpoint
import grails.plugin.jssh.JsshConfig
import grails.plugin.jssh.JsshEndpoint

class JsshGrailsPlugin {
    def version = "1.7-SNAPSHOT"
	def grailsVersion = "2.0 > *"
	def title = "j2ssh SSH Plugin"
	def author = "Vahid Hedayati"
	def authorEmail = "badvad@gmail.com"
	def description = 'Java J2SSH library combined with default websockets to provide your Grails web application with live SSH interaction functionality. Version 1 New Client/Server Websocket call: Make multiple connections and broadcast commands to a group of servers. You can either use default Websockets method which is a live interaction with your back-end ssh connection(s) or use Ajax polling which only supports 1 concurrent connection at any one time.'
	def documentation = "http://grails.org/plugin/jssh"
	def license = "APACHE"
	def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/jssh/issues']
	def scm = [url: 'https://github.com/vahidhedayati/jssh']

	def doWithSpring = {
		jsshConfig(JsshConfig) {
			grailsApplication = ref('grailsApplication')
		} 
		connectSsh(ConnectSsh) {
			
		}
	}
	
	def doWithWebDescriptor = { xml ->
		def listenerNode = xml.'listener'
		listenerNode[listenerNode.size() - 1] + {
			'listener' {
				'listener-class'(JsshEndpoint.name)
			}
			'listener' {
				'listener-class'(J2sshEndpoint.name)
			}
		}
	}
}
