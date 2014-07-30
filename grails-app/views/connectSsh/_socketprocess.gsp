
	
	<div id="pageHeader"></div>
	<form><div id="mySshBox">
		<input id="textMessage" type="text">
		<input type="button" value="send" onClick="sendMessage();">
	</div></form>

	
	<div  class="logconsolebar">	
		<div class="btn btn-primary"><b>${hostname }: running command</b></div>	
			<btn  class="btn btn-danger" onClick="closeConnection()">Close connection</btn>
		</div>
	</div>
	<div id="mySshOut">
		<pre  id="messagesTextarea" class="logconsole">
		</pre>
	</div>


<g:javascript>

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#mySshBox").html('');
		$("#mySshOut").html('');
	}
	
	var webSocket=new WebSocket("ws://localhost:8080/${meta(name:'app.name')}/j2ssh");
	var THRESHOLD = 10240;
	var chr=0;
		
	webSocket.onopen=function (message) {processOpen(message);};
	webSocket.onmessage=function(message) {processMessage(message);};
	webSocket.onclose=function(message) {processClose(message);};
	webSocket.onerror=function(message) {processError(message);};
	
	function processOpen(message) {
		$('#messagesTextarea').append('Server Connect....\n');
		webSocket.send(JSON.stringify({'user':"${username }",'password':"${password }", 'hostname':"${hostname }",  'port': "${port }", 'usercommand': "${userCommand }"})) 
	}
	
	function processMessage(message) {
	    $('#messagesTextarea').append(message.data);
	    scrollToBottom();
	}
	
	function closeConnection() {
		webSocket.send("DISCO:-");
		webSocket.onclose = function() { }
        webSocket.close();
        window.history.back();
    }
    
	function sendMessage() {
		if (textMessage.value!="close") {
			webSocket.send(textMessage.value);
			textMessage.value="";
		}else {
			websocket.close();
		}	
	}
	
	function processClose(message) {
		webSocket.send("Client disconnected......");
		$('#messagesTextarea').append("Server Disconnected... \n");
	}
	
	function processError(message) {
		$('#messagesTextarea').append(" Error.... \n");
	}
	
	function scrollToBottom() {
    	$('#messagesTextarea').scrollTop($('#messagesTextarea')[0].scrollHeight);
	}
	
	window.onbeforeunload = function() {
    	webSocket.send("DISCO:-");
        webSocket.onclose = function() { }
        webSocket.close();
    }
</g:javascript>
