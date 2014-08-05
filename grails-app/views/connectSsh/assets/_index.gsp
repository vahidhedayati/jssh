<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="ajssh"/>
    </g:if>
    <g:else>
    	 <asset:stylesheet href="jssh.css" />
    	 <asset:stylesheet href="bootstrap.min.css" />
    	 
    	 <asset:javascript src="jquery.min.js"/>
    	 <asset:javascript src="jssh.js"/>
    </g:else>
  <title><g:message code="jssh.title.label" args="[entityName]"  default="j2ssh Ajax/Poll"/></title>
</head>
<body>
<div class="container">
	<div id="pageHeader" class="page-header">
		<g:message code="jssh.${pageid }.label" args="[entityName]"  default="${pagetitle }"/>
	</div>

	
	<g:render template="/connectSsh/${loadtemplate}"  model="[hideAuthBlock:hideAuthBlock,input:input,port:port,username:username,password:password,hostname:hostname,userCommand:userCommand]"/>
</div>




</body>
</html>

