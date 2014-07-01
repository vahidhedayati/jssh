package grails.plugin.jssh

class JsshConfig {
	def grailsApplication
	def getConfig(String configProperty) {
		grailsApplication.config.jssh[configProperty] ?: ''
	}
}
