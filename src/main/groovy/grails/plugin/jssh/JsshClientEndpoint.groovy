package grails.plugin.jssh


import grails.plugin.jssh.j2ssh.JsshClientProcessService
import grails.plugin.jssh.j2ssh.JsshConfService

import javax.websocket.ClientEndpoint
import javax.websocket.CloseReason
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import grails.util.Holders

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ClientEndpoint
public class JsshClientEndpoint  extends JsshConfService {

	private final Logger log = LoggerFactory.getLogger(getClass().name)
	private Session userSession = null
	private String job = null
	private JsshClientProcessService jsshClientProcessService

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("job") String job) {
		this.userSession = userSession
		this.job = job
		def ctx = Holders.applicationContext
		jsshClientProcessService = ctx.jsshClientProcessService
		//userSession.userProperties.put("job", job)

	}

	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		jsshClientProcessService.handleClose(userSession)
		this.userSession = null
	}

	@OnMessage
	public void onMessage(String message, Session userSession){
		try {
			jsshClientProcessService.processResponse(userSession,message)
		} catch (IOException ex) {
			ex.printStackTrace()
		}
	}

	@OnError
	public void handleError(Throwable t) {
		jsshClientProcessService.handleClose(userSession)
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
