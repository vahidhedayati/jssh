<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.process.label', default: 'SSH Output')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<script src="${createLink(uri: '/js/jquery.js')}" type="text/javascript"></script>
<style type="text/css" media="screen">
.logconsolebar {
 	border: 3px groove #ccc;
  	color: #FFF; 
  	background: #000;
  	border-style: solid;
  	border-width: thin;
  	border-color: black;
  	font-size: 1em;
    width: 95%;
  	font-family: monospace;
  	margin: 6px 6px 6px 6px;
  	padding: 12px 12px 12px 12px;
   font-weight: bold;
}

.logconsole { 
	border: 3px groove #ccc;
  	color: #FFF; 
  	background: #000;
  	white-space: pre;
  	border-style: solid;
  	border-width: thin;
  	border-color: black;
  	font-size: 1em;
    width: 95%;
    height: 500px;
  	font-family: monospace;
  	display: inline-block;
  	*display:inline;
  	*zoom:1;
  	position: relative;
  	margin: 6px 6px 6px 6px;
  	padding: 12px 12px 12px 12px;
  	overflow:auto;
	resize:both;
}
			
.tbutton {
	border: 1px solid rgba(0,0,0,0.2);
	box-sizing: content-box !important;
	color: #FFF;
	width: auto;
	display: inline-block;
    *zoom: 1;
    *display: inline;
	padding: 0.01em 0.02em;
	text-align: center;
	text-decoration: none;
	white-space: normal;
	outline: none; 
	-moz-transition: all 200ms ease 0ms !important;
	-o-transition: all 200ms ease 0ms !important;
	-webkit-transition: all 200ms ease 0ms !important;
	background: none repeat scroll 0 0 rgba(255,255,255,0.04);
	border-radius: 3px;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
	-moz-background-clip: padding;
	-webkit-background-clip: padding;
	background-clip: padding-box;
	box-shadow: 0 0 3px rgba(255,255,255,0.25) inset, 0 0 1px rgba(255,255,255,0.2), 0 10px 10px rgba(255,255,255,0.08) inset;
	-moz-box-shadow: 0 0 3px rgba(255,255,255,0.25) inset, 0 0 1px rgba(255,255,255,0.2), 0 10px 10px rgba(255,255,255,0.08) inset;
	-webkit-box-shadow: 0 0 3px rgba(255,255,255,0.25) inset, 0 0 1px rgba(255,255,255,0.2), 0 10x 10px rgba(255,255,255,0.08) inset;
	box-shadow: 0 0 3px rgba(255,255,255,0.25) inset, 0 0 1px rgba(255,255,255,0.2), 0 10px 10px rgba(255,255,255,0.08) inset;
}
			
</style>
	</head>
	<body>
			
	<div class="logconsolebar">	
		<div class="tbutton"><b>${hostname }: running command</b></div>	
		<g:link class="tbutton" controller="connectSsh" action="closeConnection">Close connection</g:link>
		<div class="tbutton"><input type="checkbox" name="pauseLog"  id="pauseLog" onChange="TriggerFilter(this)"> Pause logs</div>
	</div>
	
<script>
	var t;
    function getOnline() {
    	 <g:remoteFunction action="inspection" update="inspect"/>
    }
    
    function pollPage() {
        getOnline();
         t = setTimeout('pollPage()', 5000);
    }
    function stopPage() {
        clearTimeout(t);
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
			
<div id="inspect" class="logconsole">
	${input ?: 'Please be patient - logging will start shortly' }		
</div>

</body>
</html>