package grails.plugin.jssh

class JsshDnsService {
	

	Map hostLookup(String host) {
		String hname, hip,fqdn
		try {
			def inetAddress = InetAddress?.getByName(host)
			if (inetAddress) {
				fqdn=inetAddress?.getCanonicalHostName()
				hname=inetAddress?.getHostName().toString()
				hip=inetAddress?.getHostAddress().toString()
			}
		}catch(Exception e) {
			log.error "Error occured resolveHost "+e
		}
		return [name:hname, ip:hip, fqdn:fqdn]
	}
}
