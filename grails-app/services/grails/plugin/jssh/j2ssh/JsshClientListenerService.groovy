package grails.plugin.jssh.j2ssh


import grails.converters.JSON
import grails.plugin.jssh.JsshClientEndpoint

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class JsshClientListenerService extends JsshConfService {

	static transactional  =  false

	def jsshMessagingService
	def jsshDbStorageService

	Session p_connect(String _uri, String username,  Map map){
		URI oUri
		if(_uri){
			oUri = URI.create(_uri);
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
		oSession.userProperties.put("username", username)
		if (map.jUser) {
			oSession.userProperties.put("jUser", map.jUser)
		}
		oSession.userProperties.put("realUser", map.realUser)
		oSession.userProperties.put("conLogId", map.conLogId)
		oSession.userProperties.put("conloggerId", map.conloggerId)
		oSession.userProperties.put("comloggerId", map.comloggerId)
		oSession.userProperties.put("host", map.hostname)
		oSession.userProperties.put('enablePong', map.enablePong)
		oSession.userProperties.put('pingRate', map.pingRate)


		if (map.hosts) {
			oSession.userProperties.put("hosts", map.hosts)
		}

		return  oSession
	}


	public Session disconnect(Session _oSession){
		try{
			if(_oSession && _oSession.isOpen()){
				jsshMessagingService.sendMessage(_oSession, DISCONNECTOR)
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return _oSession
	}

}
