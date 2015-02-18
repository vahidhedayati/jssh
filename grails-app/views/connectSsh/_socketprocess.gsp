<g:render template="/connectSsh/sshLayout" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}"/>

<g:javascript>     
	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#mySshBox${divId}").html(msg);
		$("#mySshOut${divId}").html('');
	}
	
 	var divId="${divId}";
 	
	var webSocket${divId}=new WebSocket('${uri}');
	var THRESHOLD = 10240;
	var chr=0;
	webSocket${divId}.onopen=function (message) {processOpen${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	var textMessage=document.getElementById("textMessage${divId}");

	
	function processOpen${divId}(message) {
		$('#messagesTextarea${divId}').append('Server Connect....\n');
		webSocket${
	divId
}.send(JSON.stringify({'frontend':"${frontend}",'jsshUser':"${jsshUser}",'user':"${username}",'password':"${password}", 'hostname':"${hostname}", 'port': "${port }" ,'enablePong':"${enablePong }",'pingRate':"${pingRate }", 'usercommand': "${userCommand}"}))
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
			if (message.data == "ping") {
				//console.log('Got ping, sending PING');
				webSocket${divId}.send('PING');
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
		webSocket${divId}.send(JSON.stringify({ 'frontend':"false",'DISCO':"true"}));
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
		if (textMessage.value!="close") {
		
			if ((textMessage${divId}.value.indexOf('\n')>-1) || (textMessage${divId}.value.indexOf('\r')>-1) ) {
				actOnEachLine(textMessage${divId}, function(line) {
   					webSocket${divId}.send(line);
				});
			}else{
				webSocket${divId}.send(textMessage${divId}.value);
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
	
	function processError${divId}(message) {
		$('#messagesTextarea${divId}').append(" Error.... \n");
	}
	
	window.onbeforeunload = function() {
    	webSocket${divId}.send("DISCO:-");
        webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
    }
</g:javascript>
