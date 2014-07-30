import grails.plugin.jssh.ConnectSsh
import grails.plugin.jssh.JsshConfig
import grails.plugin.jssh.JsshEndpoint

class JsshGrailsPlugin {
    def version = "0.5"
	def grailsVersion = "2.0 > *"
	def title = "j2ssh SSH Plugin"
	def author = "Vahid Hedayati"
	def authorEmail = "badvad@gmail.com"
	def description = 'Uses the j2ssh library to provide ssh access'
	def documentation = "http://grails.org/plugin/jssh"
	def license = "GPL2"
	def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/jssh/issues']
	def scm = [url: 'https://github.com/vahidhedayati/jssh']

	def doWithSpring = {
		jsshConfig(JsshConfig) {
			grailsApplication = ref('grailsApplication')
		} 
		connectSsh(ConnectSsh) {
			
		}
	}
}
