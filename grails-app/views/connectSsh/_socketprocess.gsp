


<div id="mySshBox">
   <div  class="form-group">
    <label for="execute" class="col-sm-1 control-label">Execute:</label>
   	<div class="col-sm-10">
	<div id="contact-area">
		<textarea cols="20" rows="3" id="textMessage${divId}" name="message"></textarea>

        <button  id="sender" class="btn btn-primary" onclick="sendMessage${divId}();">Send</button>

   </div>		
</div></div>
</div>
<div style="clear:both"></div>

		
<div  class="logconsolebar">
	<div class="btn btn-primary">
		<b>${hostname }: running <span id="whatCommand${divId}"></span></b>
	</div>	
	<btn  class="btn btn-danger" onclick="closeConnection${divId}()">Close connection</btn>
	<btn  class="btn btn-warning" onclick="Pause${divId}()">Pause</btn>
	<btn  class="btn btn-success" onclick="Resume${divId}()">Resume</btn>
</div>
	
<div id="mySshOut">
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
    var divId="${divId}";
	if (!window.WebSocket) {
		var msg = "Your browser does not have WebSocket support";
		$("#pageHeader").html(msg);
		$("#mySshBox").html('');
		$("#mySshOut").html('');
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
        window.history.back();
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
