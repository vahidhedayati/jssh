

	<div class="logconsolebar">	
		<div class="btn btn-primary"><b>${hostname }: running command</b></div>	
		<g:link  class="btn btn-danger" controller="connectSsh" action="closeConnection">Close connection</g:link>
		<div class="tbutton1"><input type="checkbox" name="pauseLog"  id="pauseLog" onChange="TriggerFilter(this)"> Pause logs</div>
		</div>

			
<pre id="inspect" class="logconsole">
	${input ?: 'Please be patient - logging will start shortly' }		
</pre>
	
<script type="text/javascript">
	var t;
	var r;
    function getOnline() {
    	 $.get('${createLink(controller:"connectSsh", action: "inspection")}',function(data){
    			$('#inspect').append(data);
    			 resetOutput();
 		});
    }
    function resetOutput() { 
    	$.get('${createLink(controller:"connectSsh", action: "resetOutput")}',function(data){
		});	
    }
    function refPage() {
   		var elem = document.getElementById('inspect');
  		elem.scrollTop = elem.scrollHeight; 
    }
    function pollPage() {
        getOnline();
       
         t = setTimeout('pollPage()', 5000);
         r= setTimeout('refPage()', 1000);
    }
    function stopPage() {
        clearTimeout(t);
        clearTimeout(r);
    }
 	pollPage();
 	function TriggerFilter(e) {
		if (e.checked==true) {
    		stopPage();
    	}	else{
    		pollPage();
    	}
    };
</script>
