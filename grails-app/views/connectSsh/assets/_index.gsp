<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="ajssh"/>
    </g:if>
    <g:else>
    	 <asset:stylesheet href="jssh.css" />
    	 <asset:stylesheet href="bootstrap.min.css" />
    	 <g:javascript library="jquery"/>
    </g:else>
  <title><g:message code="jssh.title.label" args="[entityName]"  default="j2ssh Ajax/Poll"/></title>
</head>
<body>
<g:render template="/wsChat/${loadtemplate}"/>
</body>
</html>
