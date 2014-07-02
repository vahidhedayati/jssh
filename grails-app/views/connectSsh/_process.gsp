<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.process.label', default: 'SSH Output')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<link rel="stylesheet" href="${createLink(uri: '/css/bootstrap.min.css')}" type="text/css">
		<link rel="stylesheet" href="${createLink(uri: '/css/jssh.css')}" type="text/css">
		<script src="${createLink(uri: '/js/jquery.js')}" type="text/javascript"></script>

	</head>
	<body>
			
	<div class="logconsolebar">	
		<div class="btn btn-primary"><b>${hostname }: running command</b></div>	
		<g:link  class="btn btn-danger" controller="connectSsh" action="closeConnection">Close connection</g:link>
		<div class="tbutton"><input type="checkbox" name="pauseLog"  id="pauseLog" onChange="TriggerFilter(this)"> Pause logs</div>
	</div>

			
<div id="inspect" class="logconsole">
	${input ?: 'Please be patient - logging will start shortly' }		
</div>
	
<script type="text/javascript">
	var t;
	var r;
    function getOnline() {
    	 <g:remoteFunction action="inspection" update="inspect"/>
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

</body>
</html>