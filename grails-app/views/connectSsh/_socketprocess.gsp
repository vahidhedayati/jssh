


<div id="mySshBox">
   <div  class="form-group">
    <label for="execute" class="col-sm-1 control-label">Execute:</label>
   	<div class="col-sm-10">
	<div id="contact-area">
		<textarea cols="20" rows="3" id="textMessage" name="message"></textarea>

        <button  id="sender" class="btn btn-primary" onclick="sendMessage();">Send</button>

   </div>		
</div></div>
</div>
<div style="clear:both"></div>

		
<div  class="logconsolebar">
	<div class="btn btn-primary">
		<b>${hostname }: running <span id="whatCommand"></span></b>
	</div>	
	<btn  class="btn btn-danger" onclick="closeConnection()">Close connection</btn>
	<btn  class="btn btn-warning" onclick="Pause()">Pause</btn>
	<btn  class="btn btn-success" onclick="Resume()">Resume</btn>
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
	
	var webSocket=new WebSocket("ws://${wshostname}/${meta(name:'app.name')}/j2ssh");
	var THRESHOLD = 10240;
	var chr=0;
		
	webSocket.onopen=function (message) {processOpen(message);};
	webSocket.onmessage=function(message) {processMessage(message);};
	webSocket.onclose=function(message) {processClose(message);};
	webSocket.onerror=function(message) {processError(message);};
	var textMessage=document.getElementById("textMessage");
	function processOpen(message) {
		$('#messagesTextarea').append('Server Connect....\n');
		webSocket.send(JSON.stringify({'user':"${username }",'password':"${password }", 'hostname':"${hostname }",  'port': "${port }", 'usercommand': "${userCommand }"}))
		$('#whatCommand').html("${userCommand }"); 
	}
	
	function processMessage(message) {
	    $('#messagesTextarea').append(message.data);
	    scrollToBottom();
	}
	
	
	function Pause() {
		webSocket.send("PAUSE:-");
    }
    
	function Resume() {
		webSocket.send("RESUME:-");
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
			$('#whatCommand').html(textMessage.value);
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
	
	$('#textMessage').keypress(function(e){
  		if (e.keyCode == 13 && !e.shiftKey) {
     		e.preventDefault();
		}
		if(e.which == 13){
       		var tmb=textMessage.value.replace(/^\s*[\r\n]/gm, "");
       		if (tmb!="") {
       			sendMessage();
        	}
       }
     });
     
	window.onbeforeunload = function() {
    	webSocket.send("DISCO:-");
        webSocket.onclose = function() { }
        webSocket.close();
    }
</g:javascript>
