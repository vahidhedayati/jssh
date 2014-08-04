
<g:if test="${((!hideSendBlock) || (!hideSendBlock.equals('YES')))}">	
	<g:javascript>
		toggleBlock('#sendCommand${divId}','#mySshBox${divId}','REMOTE EXECUTOR')
	</g:javascript>
	 <div class="pull-right btn btn-default"><a id="sendCommand${divId}">SHOW REMOTE EXECUTOR</a></div>
</g:if>


<g:if test="${((!hideConsoleMenu) || (!hideConsoleMenu.equals('YES')))}">	
	<g:javascript>
		toggleBlock('#consoleMenu${divId}','#consoleMenuBar${divId}','CONSOLE MENU')
	</g:javascript>
 <div class="pull-right btn btn-default"><a id="consoleMenu${divId}">HIDE CONSOLE MENU</a></div>
</g:if>
 

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
				<b>${hostname }: running <span id="whatCommand${divId}"></span></b>
			</div>
		</g:if>
	
		<g:if test="${((!hideDiscoButton) || (!hideDiscoButton.equals('YES')))}">
			<btn  class="btn btn-danger btn-xs" onclick="closeConnection${divId}()" title="Close SSH Connection">Close connection</btn>
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
				<b>${hostname }: running <span id="whatCommand${divId}"></span></b>
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
		<pre  id="messagesTextarea${divId}" class="logconsole-sm">
		</pre>
	</g:if>
	<g:else>
		<pre  id="messagesTextarea${divId}" class="logconsole-lg">
		</pre>
	</g:else>
</div>

<g:javascript>


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
	
	var webSocket${divId}=new WebSocket("ws://${wshostname}/${meta(name:'app.name')}/j2ssh");
	var THRESHOLD = 10240;
	var chr=0;
		
	webSocket${divId}.onopen=function (message) {processOpen${divId}(message);};
	webSocket${divId}.onmessage=function(message) {processMessage${divId}(message);};
	webSocket${divId}.onclose=function(message) {processClose${divId}(message);};
	webSocket${divId}.onerror=function(message) {processError${divId}(message);};
	var textMessage=document.getElementById("textMessage${divId}");
	function processOpen${divId}(message) {
		$('#messagesTextarea${divId}').append('Server Connect....\n');
		webSocket${divId}.send(JSON.stringify({'user':"${username }",'password':"${password }", 'hostname':"${hostname }",  'port': "${port }", 'usercommand': "${userCommand }"}))
		$('#whatCommand${divId}').html("${userCommand }"); 
	}
	
	function processMessage${divId}(message) {
	    $('#messagesTextarea${divId}').append(message.data);
	    scrollToBottom${divId}();
	}
	
	function SameSess${divId}() {
	 	webSocket${divId}.send("SAME_SESSION:-");
	}
	
	function NewSess${divId}() {
	 	webSocket${divId}.send("NEW_SESSION:-");
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
			webSocket${divId}.send(textMessage${divId}.value);
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
    	$('#messagesTextarea${divId}').scrollTop($('#messagesTextarea${divId}')[0].scrollHeight);
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
