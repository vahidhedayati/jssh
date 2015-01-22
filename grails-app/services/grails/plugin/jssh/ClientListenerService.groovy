package grails.plugin.jssh


import grails.converters.JSON

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ClientListenerService extends ConfService {

	def messagingService

	Session p_connect(String _uri, String _username, String room, Map map){
		URI oUri
		if(_uri){
			oUri = URI.create(_uri+room);
		}
		def container = ContainerProvider.getWebSocketContainer()
		Session oSession
		try{
			oSession = container.connectToServer(JsshClientEndpoint.class, oUri)
			oSession.basicRemote.sendText((map as JSON).toString())
		}catch(Exception e){
			e.printStackTrace()
			if(oSession && oSession.isOpen()){
				oSession.close()
			}
			return null
		}
		oSession.userProperties.put("username", _username)
		return  oSession
	}


	public Session disconnect(Session _oSession){
		try{
			if(_oSession && _oSession.isOpen()){
				messagingService.sendMessage(_oSession, DISCONNECTOR)
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return _oSession
	}

}
