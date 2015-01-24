
<g:render template="/connectSsh/sshConnectLayout" />

<g:javascript>

	var loggedInUsers=[];
	var user="${user }";
	var backuser='${backuser}';
	var uri="${uri}";
	var divId="${divId}";
	
	
	var webSocket${divId}=new WebSocket(uri);
	webSocket${divId}.onopen=function(message) {processOpen${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);	};

	var userList=[];
	
	
	function processOpen${divId}(message) {
		$('#messagesTextarea${divId}').append('Client Socket connected to Socket Server....\n');
		webSocket${
	divId
}.send(JSON.stringify({'client': 'yes', 'frontend':"${frontend}",'jsshUser':"${frontuser}"}))
	}
		function processMessage${divId}(message) {
			//console.log(JSON.stringify(message));
			//console.log(message);
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
			if (message.data == "ping") {
				webSocket${divId}.send('PONG');
			}else{
				$('#messagesTextarea${divId}').append(escapeHtml(message.data));
				scrollToBottom${divId}();
			}
		}
	}
	
	function SameSess${divId}() {
	 	webSocket${divId}.send("SAME_SESSION:-");
	}
	
	function NewSess${divId}() {
	 	webSocket${divId}.send("NEW_SESSION:-");
	}
	
	function NewShell${divId}() {
		webSocket${divId}.send("NEW_SHELL:-");
    }
    
    function CloseShell${divId}() {
		webSocket${divId}.send("CLOSE_SHELL:-");
    }
    
	function Pause${divId}() {
		webSocket${divId}.send("PAUSE:-");
    }
    
	function Resume${divId}() {
		webSocket${divId}.send("RESUME:-");
    }
    
	function closeConnection${divId}() {
		//webSocket${divId}.send("DISCO:-");
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true"}));
		webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
        if (divId=="Basic") {
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
   					webSocket${divId}.send('/fm '+backuser+','+line);
				});
			}else{
				webSocket${divId}.send('/fm '+backuser+','+textMessage${divId}.value);
			}
			
			$('#whatCommand${divId}').html(textMessage${divId}.value);
			textMessage${divId}.value="";
		}else {
			webSocket${divId}.close();
		}	
	}
	
	function processClose${divId}(message) {
		webSocket${divId}.send("Client disconnected......");
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
		var idx = loggedInUsers.indexOf(uid);
		if (idx != -1) {
			ison="true";
		}
		return ison;
	}	

	
	window.onbeforeunload = function() {
		//webSocket${divId}.onclose = function() { }
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true"}));
		webSocket${divId}.close();
	}
</g:javascript>
