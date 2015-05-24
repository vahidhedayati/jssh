package jssh

import org.springframework.boot.context.embedded.ServletContextInitializer
import org.springframework.context.annotation.Bean

import javax.servlet.ServletContext
import javax.servlet.ServletException
import grails.plugin.jssh.J2sshEndpoint

class DefaultJsshCfg {

	@Bean
	public ServletContextInitializer myInitializer() {
		return new ServletContextInitializer() {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.addListener(J2sshEndpoint)


			}
		}
	}

}
