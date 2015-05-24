package grails.plugin.jssh.interfaces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

public interface JsshBase {

	static final ConcurrentMap<String, Session> sshUsers = new ConcurrentHashMap<String, Session>();
}
