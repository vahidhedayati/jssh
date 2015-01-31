<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets" />
</g:if>
<g:else>
	<g:render template="/resources" />
</g:else>
<div class="dark">

	
	<div id="mySshBox${divId}">
		<div class="form-group">
			<label for="execute" class="col-sm-1 control-label">Broadcast Execute:</label>
			<div class="col-sm-10">
				<div id="contact-area">
					<textarea cols="20" rows="1" id="textMessage${divId}"
						name="message"></textarea>
					<button class="btn btn-primary sender"
						onclick="broadcastMessage${divId}();">Send</button>
				</div>
			</div>
		</div>
	</div>
</div>

<g:javascript>

	var ${divId}loggedInUsers=[];
	
	var webSocket${divId}=new WebSocket('${uri}');
	webSocket${divId}.onopen=function(message) {processOpen${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);	};

	
	function processOpen${divId}(message) {
		webSocket${divId}.send(JSON.stringify({'client': 'yes', 'frontend':"${frontend}",'jsshUser':"${frontuser}"}))
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
	
    function processClose${divId}(message) {
		webSocket${divId}.send(JSON.stringify({ 'frontend':"true",'DISCO':"true", 'system': 'disconnect'}));
		webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
	}
	
    function broadcastMessage${divId}() {
		webSocket${divId}.send('/bcast ${jobName},'+textMessage${divId}.value);
		textMessage${divId}.value="";
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
		webSocket${divId}.close();
	}
</g:javascript>
