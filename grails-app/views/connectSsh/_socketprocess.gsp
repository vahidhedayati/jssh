<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.process.label', default: 'SSH Output')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<link rel="stylesheet" href="${createLink(uri: '/css/bootstrap.min.css')}" type="text/css">
		<link rel="stylesheet" href="${createLink(uri: '/css/jssh.css')}" type="text/css">
		<script src="${createLink(uri: '/js/jquery.min.js')}" type="text/javascript"></script>
	</head>
	<body>
	
	<div id="pageHeader"></div>
	<form><div id="mySshBox">
		<input id="textMessage" type="text">
		<input type="button" value="send" onClick="sendMessage();">
	</div></form>

	
	<div  class="logconsolebar">	
		<div class="btn btn-primary"><b>${hostname }: running command</b></div>	
			<g:link  class="btn btn-danger" controller="connectSsh" action="closeConnection">Close connection</g:link>
			<div class="tbutton1"><input type="checkbox" name="pauseLog"  id="pauseLog" onChange="TriggerFilter(this)"> Pause logs</div>
		</div>
	</div>
	<div id="mySshOut">
			<textarea id="messagesTextarea"  class="logconsole" readonly="readonly" rows="20%" cols="100%">
			</textarea>

	</div>


<g:javascript>

	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#mySshBox").html('');
		$("#mySshOut").html('');
	}
	
	var webSocket=new WebSocket("ws://localhost:8080/${meta(name:'app.name')}/j2ssh");
	var messagesTextarea=document.getElementById("messagesTextarea");
	var THRESHOLD = 10240;
	var chr=0;
		
	webSocket.onopen=function (message) {processOpen(message);};
	webSocket.onmessage=function(message) {processMessage(message);};
	webSocket.onclose=function(message) {processClose(message);};
	webSocket.onerror=function(message) {processError(message);};
	
	function processOpen(message) {
		messagesTextarea.value +=" Server Connect.... "+"\n";
		webSocket.send(JSON.stringify({'user':"${username }",'password':"${password }", 'hostname':"${hostname }",  'port': "${port }", 'usercommand': "${userCommand }"})) 
	}
	
	function processMessage(message) {
		if (message.data=="SSH_SUCCESS") {
			webSocket.send('_internal_process');
		}else{
			if(message.data instanceof ArrayBuffer) {
		    	var wordarray = new Uint16Array(message.data);
				for (var i = 0; i < wordarray.length; i++) {
					console.log(wordarray[i]);
				    wordarray[i]=wordarray[i]+1;
				}
				messagesTextarea.value +=""+wordarray.buffer+"\n"
		    }else{
			    messagesTextarea.value +=""+message.data+"\n"
			}
		}	
	}
	
	function sendMessage() {
		if (textMessage.value!="close") {
			webSocket.send(textMessage.value);
			messagesTextarea.value +=" Send to Server ===> ("+ textMessage.value +")\n";
			textMessage.value="";
			scrollToBottom();
		}else {
			websocket.close();
		}	
	}
	
	function processClose(message) {
		webSocket.send("Client disconnected......");
		messagesTextarea.value +="Server Disconnected... "+"\n";
	}
	
	function processError(message) {
		messagesTextarea.value +=" Error.... \n";
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

	</body>
	</html>