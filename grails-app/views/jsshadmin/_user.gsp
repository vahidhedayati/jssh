<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jsshuser.label', default: 'Jssh user List')}" />
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
					 <div class="col-md-2">	<g:sortableColumn property="username" title="${message(code: 'jsshuser.username.label', default: 'Jssh UserName')}" /></div>
						 <div class="col-md-1"><g:sortableColumn property="permissions" title="${message(code: 'jsshuser.permissions.label', default: 'permissions')}" /></div>
						 <div class="col-md-1"><g:sortableColumn property="conlog" title="${message(code: 'jsshuser.conlog.label', default: 'conlog')}" /></div>
						 <div class="col-md-2"><g:sortableColumn property="sshuser" title="${message(code: 'jsshuser.sshuser.label', default: 'sshuser')}" /></div>
						 <div class="col-md-3"><g:sortableColumn property="servers" title="${message(code: 'jsshuser.servers.label', default: 'servers')}" /></div>
						 <div class="col-md-2"><g:sortableColumn property="groups" title="${message(code: 'jsshuser.owner.label', default: 'groups')}" /></div>
					</div>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
						<div class="col-md-2"><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></div>
						<div class="col-md-1">${fieldValue(bean: userInstance, field: "permissions")}</div>
						<div class="col-md-1">${fieldValue(bean: userInstance, field: "conlog")}</div>
						
						<div class="col-md-2">
							<jssh:sshList rtype="sshuser" ilist="${userInstance.sshuser}"/>
						</div>
						
						<div class="col-md-3">
							<jssh:sshList rtype="servers" ilist="${userInstance.servers}"/>
						</div>
						
						<div class="col-md-2">
							<jssh:sshList rtype="groups" ilist="${userInstance.groups}"/>
						</div>
					</div>
				</g:each>
				
			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div>
		</div>
	</body>
</html>