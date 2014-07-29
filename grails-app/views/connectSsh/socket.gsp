<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.input.label', default: 'SSH Input')}" />
		<link rel="stylesheet" href="${createLink(uri: '/css/bootstrap.min.css')}" type="text/css">
		<link rel="stylesheet" href="${createLink(uri: '/css/jssh.css')}" type="text/css">
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
	
		<div class="container">
			<div class="btn btn-danger"><g:link action="index">Ajax poll</g:link></div>
			<g:form method="post" action="socketprocess">
			<g:textField name="username" placeholder="Username?"/>
			<g:passwordField name="password" placeholder="password?"/>
			<g:textField name="hostname" placeholder="hostname?"/>
			<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
			<g:actionSubmit class="btn btn-primary" value="socketprocess"/>
			</g:form>
		</div>
	</body>
</html>