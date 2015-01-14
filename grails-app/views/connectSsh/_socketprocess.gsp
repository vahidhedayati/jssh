<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
   	<g:render template="/assets"/>
</g:if>
<g:else>
	<g:render template="/resources"/>
</g:else>    
<g:if test="${((!hideSendBlock) || (!hideSendBlock.equals('YES')))}">	

	 <div class="pull-right btn btn-default"><a id="sendCommand${divId}">SHOW REMOTE EXECUTOR</a></div>
	 	<g:javascript>
		toggleBlock('#sendCommand${divId}','#mySshBox${divId}','REMOTE EXECUTOR');
	</g:javascript>
</g:if>


<g:if test="${((!hideConsoleMenu) || (!hideConsoleMenu.equals('YES')))}">	

 <div class="pull-right btn btn-default"><a id="consoleMenu${divId}">HIDE CONSOLE MENU</a></div>
</g:if>
 
	<g:javascript>
		toggleBlock('#consoleMenu${divId}','#consoleMenuBar${divId}','CONSOLE MENU');
	</g:javascript>
<g:if test="${((!hideSendBlock) || (!hideSendBlock.equals('YES')))}">	
	<div style="clear:both;"></div>
	<div id="mySshBox${divId}"  style="display: none;">
   	<div  class="form-group">
    	<label for="execute" class="col-sm-1 control-label">Execute:</label>
   		<div class="col-sm-10">
		<div id="contact-area">
			<textarea cols="20" rows="1" id="textMessage${divId}" name="message"></textarea>
        	<button  class="btn btn-primary sender" onclick="sendMessage${divId}();">Send</button>
   		</div>		
	</div></div></div>
</g:if>



	<div style="clear:both"></div>
	<div  class="logconsolebar" id="consoleMenuBar${divId}" >
	<g:if test="${((!hideConsoleMenu) || (!hideConsoleMenu.equals('YES')))}">		
		<g:if test="${((!hideWhatsRunning) || (!hideWhatsRunning.equals('YES')))}">
			<div class="btn btn-primary btn-xs" title="${hostname } ssh connection">
				<b>${hostname }: running <span id="whatCommand${divId}"></span></b> |
				CONN_COUNT: <span id="connectionCount${divId}"></span>
			</div>
		</g:if>
	
		
		<g:if test="${((!hideDiscoButton) || (!hideDiscoButton.equals('YES')))}">
			<btn  class="btn btn-danger btn-xs" onclick="closeConnection${divId}()" title="Close SSH Connection">Close connection</btn>
		</g:if>
		
		<g:if test="${((!hideNewShellButton) || (!hideNewShellButton.equals('YES')))}">
			<btn  class="btn btn-success btn-xs" onclick="NewShell${divId}()" title="New Shell">New Shell</btn>
			<btn  class="btn btn-primary btn-xs" onclick="CloseShell${divId}()" title="New Shell">Close Shell</btn>
		</g:if>	
			
		<g:if test="${((!hidePauseControl) || (!hidePauseControl.equals('YES')))}">	
			<btn  class="btn btn-warning btn-xs" onclick="Pause${divId}()" title="Pause SSH Connection">Pause</btn>
			<btn  class="btn btn-success btn-xs" onclick="Resume${divId}()" title="Resume SSH Connection">Resume</btn>
		</g:if>
	
		<g:if test="${((!hideSessionCtrl) || (!hideSessionCtrl.equals('YES')))}">		
			<btn  class="btn btn-danger btn-xs " onclick="NewSess${divId}()" title="New SSH Login per command sent">New CONN_PT</btn>
			<btn  class="btn btn-primary btn-xs" onclick="SameSess${divId}()" title="Same SSH Login per command sent">Same CONN_PT</btn>
		</g:if>
	</g:if>	
	<g:else>
		<g:if test="${((!hideWhatsRunning) || (!hideWhatsRunning.equals('YES')))}">
			<div class="btn btn-primary btn-xs" title="${hostname } ssh connection">
				<b>${hostname }: running <span id="whatCommand${divId}"></span></b> | 
				CONN_COUNT: <span id="connectionCount${divId}"></span>
			</div>
		</g:if>
		<g:if test="${((!hideDiscoButton) || (!hideDiscoButton.equals('YES')))}">
			<btn  class="btn btn-danger btn-xs" onclick="closeConnection${divId}()" title="Close SSH Connection">Close connection</btn>
		</g:if>	
		<g:if test="${((!hidePauseControl) || (!hidePauseControl.equals('YES')))}">	
			<btn  class="btn btn-warning btn-xs" onclick="Pause${divId}()" title="Pause SSH Connection">Pause</btn>
			<btn  class="btn btn-success btn-xs" onclick="Resume${divId}()" title="Resume SSH Connection">Resume</btn>
		</g:if>
	</g:else>
	</div>
	
<div id="mySshOut${divId}">
	<g:if test="${divId}">
		<pre  class="logconsole-sm" id="preTextarea${divId}">
		<code id="messagesTextarea${divId}">
		</code>
		</pre>
	</g:if>
	<g:else>
		<pre  class="logconsole-lg" id="preTextarea${divId}">
		<code id="messagesTextarea${divId}">
		</code>
		</pre>
	</g:else>
</div>

<g:javascript>
function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
}



function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
 }



// Should be loaded from higher level
// added just incase this page is called 
// from outside of the scope of default plugin method
function toggleBlock(caller,called,calltext) {
	$(caller).click(function() {
		if($(called).is(":hidden")) {
 			$(caller).html('HIDE '+calltext).fadeIn('slow');
    	}else{
        	$(caller).html('SHOW '+calltext).fadeIn('slow');	
        	
    	}
 		$(called).slideToggle("fast");
 	
  	});
  }	


    var divId="${divId}";
	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#mySshBox${divId}").html(msg);
		$("#mySshOut${divId}").html('');
	}

	<g:if test="${addAppName=='NO'}">
		var wsurl="ws://${wshostname}/j2ssh"
	</g:if>
	<g:else>
		var wsurl="ws://${wshostname}/${meta(name:'app.name')}/j2ssh"	
	</g:else>
	
	
	var webSocket${divId}=new WebSocket(wsurl);
	var THRESHOLD = 10240;
	var chr=0;
	webSocket${divId}.onopen=function (message) {processOpen${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	var textMessage=document.getElementById("textMessage${divId}");
	
	function processOpen${divId}(message) {
		$('#messagesTextarea${divId}').append('Server Connect....\n');
		webSocket${divId}.send(JSON.stringify({'user':"${username }",'password':"${password }", 'hostname':"${hostname }", 'port': "${port }", 'usercommand': "${userCommand}"}))
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
			
			$('#messagesTextarea${divId}').append(escapeHtml(message.data));
			scrollToBottom${divId}();
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
		webSocket${divId}.send("DISCO:-");
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
	
	function scrollToBottom${divId}() {
    	//$('#messagesTextarea${divId}').scrollTop($('#messagesTextarea${divId}')[0].scrollHeight);
    	$('#preTextarea${divId}').scrollTop($('#preTextarea${divId}')[0].scrollHeight);
	}
	

	function actOnEachLine(textarea, func) {
    	var lines = textarea.value.replace(/\r\n/g, "\n").split("\n");
    	var newLines, newValue, i;

    	// Use the map() method of Array where available 
    	if (typeof lines.map != "undefined") {
 	       newLines = lines.map(func);
    	} else {
        	newLines = [];
        	i = lines.length;
        	while (i--) {
            	newLines[i] = func(lines[i]);
        	}
    	}
    	textarea.value = newLines.join("\r\n");
	}


	$('#textMessage${divId}').keypress(function(e){
  		if (e.keyCode == 13 && !e.shiftKey) {
     		e.preventDefault();
		}
		if(e.which == 13){
       		var tmb=textMessage.value.replace(/^\s*[\r\n]/gm, "");
       		if (tmb!="") {
       			sendMessage${divId}();
        	}
       }
     });
     
	window.onbeforeunload = function() {
    	webSocket${divId}.send("DISCO:-");
        webSocket${divId}.onclose = function() { }
        webSocket${divId}.close();
    }
</g:javascript>
