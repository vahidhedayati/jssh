package grails.plugin.jssh


class JsshPermissions {
	
	Date dateCreated
	Date lastUpdated
	String name
	
	static hasMany=[JsshUser]
	
	static constraints = {
		name blank: false, unique: true
	}
	
	String toString() {
		"${name}"
	}
	
}

