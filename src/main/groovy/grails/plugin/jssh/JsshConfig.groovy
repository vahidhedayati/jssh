package grails.plugin.jssh

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

class JsshConfig implements GrailsApplicationAware {

	GrailsApplication grailsApplication


	public ConfigObject getConfig() {
		return grailsApplication.config.jssh ?: ''

	}

}
