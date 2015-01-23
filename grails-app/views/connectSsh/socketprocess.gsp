<!DOCTYPE html>
<html>

<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:if test="${!request.xhr }">
		<meta name='layout' content="ajssh" />
	</g:if>
	<g:else>
		<g:render template="/assets" />
	</g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
		<meta name='layout' content="jssh" />
	</g:if>
	<g:else>
		<g:render template="/resources" />
	</g:else>
</g:else>

<title>
	${sshTitle}
</title>
</head>
<body>
<div class="container">
<g:render template="/connectSsh/navbar"/>

	<jssh:socketconnect hostname="${hostname}" username="${username}" 
port="${port}"
password="${password}" 
userCommand="${userCommand.encodeAsJavaScript()}"
jsshUser = "${jsshUser}"
divId="${divId}"
/>
<div id="${divId}"></div>
</div>

</body>
</html>