<!DOCTYPE html>
<html>
<head>
  <g:if test="${!request.xhr }">
    	<meta name='layout' content="jssh"/>
  </g:if>
  <g:else>
<g:render template="/resources" />
	<g:javascript library="jquery"/>
  </g:else>
  <title><g:message code="jssh.title.label" args="[entityName]"  default="j2ssh Ajax/Poll"/></title>
</head>
<body>
<div class="container">
	<div id="pageHeader" class="page-header">
		<g:message code="jssh.${pageid }.label" args="[entityName]"  default="${pagetitle }"/>
	</div>
	
	<g:render template="/connectSsh/${loadtemplate}" model="[input:input,port:port,username:username,password:password,hostname:hostname,userCommand:userCommand]"/>
	
</div>

<g:javascript>
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
</g:javascript>



</body>
</html>