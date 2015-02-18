<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jsshuser.label', default: 'Jssh Servers List')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<g:render template="/connectSsh/jsshAdmin" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }"/>
	</head>
	<body>
	
		<a href="#list-jsshuser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default=""/></a>

		<div id="list-jsshuser" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<div class="row">
					 <div class="col-md-2">	<g:sortableColumn property="hostName" title="${message(code: 'jsshuser.username.label', default: 'hostName')}" /></div>
						 <div class="col-md-1"><g:sortableColumn property="ipAddress" title="${message(code: 'jsshuser.ipAddress.label', default: 'ipAddress')}" /></div>
						 <div class="col-md-1"><g:sortableColumn property="sshPort" title="${message(code: 'jsshuser.sshPort.label', default: 'sshPort')}" /></div>
						 <div class="col-md-2"><g:sortableColumn property="sshuser" title="${message(code: 'jsshuser.sshuser.label', default: 'sshuser')}" /></div>
						
					</div>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
						<div class="col-md-2"><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "hostName")}</g:link></div>
						<div class="col-md-1">${fieldValue(bean: userInstance, field: "ipAddress")}</div>
						<div class="col-md-1">${fieldValue(bean: userInstance, field: "sshPort")}</div>
						<div class="col-md-2">${fieldValue(bean: userInstance, field: "sshuser")}</div>
												
					</div>
				</g:each>
				
			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
