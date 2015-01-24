package grails.plugin.jssh


import javax.websocket.ClientEndpoint
import javax.websocket.CloseReason
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ClientEndpoint
public class JsshClientEndpoint  extends ConfService {

	private final Logger log = LoggerFactory.getLogger(getClass().name)
	private Session userSession = null
	private String job = null
	private ClientProcessService clientProcessService

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("job") String job) {
		this.userSession = userSession
		this.job = job
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		
		clientProcessService = ctx.clientProcessService
		//userSession.userProperties.put("job", job)
		
	}

	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		clientProcessService.handleClose(userSession)
		this.userSession = null
	}

	@OnMessage
	public void onMessage(String message, Session userSession){
		try {
			clientProcessService.processResponse(userSession,message)
		} catch (IOException ex) {
			ex.printStackTrace()
		}
	}

	@OnError
	public void handleError(Throwable t) {
		clientProcessService.handleClose(userSession)
		t.printStackTrace()
	}
	
	
	
	public void sendMessage(final String message) {
		userSession.basicRemote.sendText(message)
	}

	public Session returnSession() {
		return userSession
	}

	public static interface MessageHandler {
		public void handleMessage(String message)
	}

}
