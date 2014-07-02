<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.input.label', default: 'SSH Input')}" />
		<link rel="stylesheet" href="${createLink(uri: '/css/bootstrap.min.css')}" type="text/css">
		<link rel="stylesheet" href="${createLink(uri: '/css/jssh.css')}" type="text/css">
		<title><g:message code="default.list.label" args="[entityName]" /></title>

		<div class="container">
			<g:form method="post" action="process">
			<g:textField name="username" placeholder="Username?"/>
			<g:textField name="password" placeholder="password?"/>
			<g:textField name="hostname" placeholder="hostname?"/>
			<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
			<g:actionSubmit class="btn btn-primary" value="process"/>
			</g:form>
		</div>
	</head>
</html>