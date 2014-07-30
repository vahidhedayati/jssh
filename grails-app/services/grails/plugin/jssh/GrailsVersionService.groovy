package grails.plugin.jssh

class GrailsVersionService {
	def grailsApplication

	double getGrailsVersion() {
		def appVersion=grailsApplication.metadata['app.grails.version']
		double verify=0.0
		if (appVersion && appVersion.indexOf('.')>-1) {
			int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
			verify=appVersion.substring(0,lastPos) as double
		}
		return verify
	}
}
