package grails.plugin.jssh.j2ssh

class JsshDnsService {

	static transactional  =  false

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
