
<g:render template="/connectSsh/sshConnectLayout" />

<g:javascript>

	var ${divId}loggedInUsers=[];
	
	var webSocket${divId}=new WebSocket('${uri}');
	webSocket${divId}.onopen=function(message) {processOpen${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);	};

	
	function processOpen${divId}(message) {
		$('#messagesTextarea${divId}').append('Client Socket connected to Socket Server....\n');
		webSocket${divId}.send(JSON.stringify({'client': 'yes', 'frontend':"${frontend}",'jsshUser':"${frontuser}"}))
	}
	
	function processMessage${divId}(message) {	
		var json;
		try {
	  		json = JSON.parse(message.data);
		} catch (exception) {
	  		json = null;
		}
		if(json) {
			var jsonData=JSON.parse(message.data);
			$('#connectionCount${divId}').html(jsonData.connCount);
		}else{
			if (message.data == "/bm ${frontuser},ping") {
				webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'COMMAND':"true", 'system': 'PONG'}));
			}else{
				if ((message.data != undefined)||(message.data != "")) {
					$('#messagesTextarea${divId}').append(escapeHtml(message.data));
					scrollToBottom${divId}();
				}
			}
		}
	}
	
	function Pause${divId}() {
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'COMMAND':"true", 'system': 'PAUSE:-'}));
    }
    
	function Resume${divId}() {
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'COMMAND':"true", 'system': 'RESUME:-'}));
    }
    
	function closeConnection${divId}() {
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true", 'system': 'disconnect'}));
		webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
        if ((${divId}!= undefined) && (${divId}=="Basic")) {
        	window.history.back();
        }else{
        	$("#mySshBox${divId}").html('');
			$("#mySshOut${divId}").html('');
        }
        	
    }

	function sendMessage${divId}() {
		if (textMessage${divId}.value!="close") {
			if ((textMessage${divId}.value.indexOf('\n')>-1) || (textMessage${divId}.value.indexOf('\r')>-1) ) {
				actOnEachLine(textMessage${divId}, function(line) {
   					webSocket${divId}.send('/fm ${backuser},'+line);
				});
			}else{
				webSocket${divId}.send('/fm ${backuser}@${hostname},'+textMessage${divId}.value);
			}
			
			$('#whatCommand${divId}').html(textMessage${divId}.value);
			textMessage${divId}.value="";
		}else {
			webSocket${divId}.close();
		}	
	}
	
	function processClose${divId}(message) {
		//webSocket${divId}.send("Client disconnected......");
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true", 'system': 'disconnect'}));
		webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
		$('#messagesTextarea${divId}').append("Server Disconnected... \n");
	}
	
	function isJson(message) {
		var input='{';
		return new RegExp('^' + input).test(message);
	}
	function isPm(message) {
		var input='/pm';
		return new RegExp('^' + input).test(message);
	}
	
	function isReceivedMsg(message) {
		var input='_received';
		return new RegExp( input +'$').test(message);
	}
	
	function processError${divId}(message) {
		console.log(message);
	}
	
	function verifyIsOn(uid) {
		var ison="false";
		var idx = ${divId}loggedInUsers.indexOf(uid);
		if (idx != -1) {
			ison="true";
		}
		return ison;
	}	

	
	window.onbeforeunload = function() {
		//webSocket${divId}.onclose = function() { }
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true", 'system': 'disconnect'}));
		//webSocket${divId}.close();
	}
</g:javascript>
