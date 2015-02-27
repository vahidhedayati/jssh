grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
		mavenRepo "https://repo.grails.org/grails/plugins"
	}

	dependencies {
		compile 'sshtools:j2ssh-core:0.2.9'
		compile ('javax.websocket:javax.websocket-api:1.0') { export = false }
	}

	plugins {
		
		runtime ":pluginbuddy:0.3"
		
		runtime ":hibernate4:4.3.6.1", {
			export = false
		}
		
		build ':release:3.0.1', ':rest-client-builder:2.0.3', {
			export = false
		}
	}
}
