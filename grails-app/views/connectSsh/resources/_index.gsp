<!DOCTYPE html>
<html>
<head>
  <g:if test="${!request.xhr }">
    	<meta name='layout' content="jssh"/>
  </g:if>
  <g:else>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'jssh.css')}" type="text/css">
	<g:javascript library="jquery"/>
  </g:else>
  <title><g:message code="jssh.title.label" args="[entityName]"  default="j2ssh Ajax/Poll"/></title>
</head>
<body>
<g:render template="/connectSsh/${loadtemplate}" model="[input:input,port:port,username:username,password:password,hostname:hostname,userCommand:userCommand]"/>
</body>
</html>