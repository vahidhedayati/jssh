<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'jsshuser.label', default: 'Jssh User')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<g:render template="/connectSsh/jsshAdmin"
	model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }" />
</head>
<body>
<div class="container">
		<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-success"	onclick="javascript:addJsshUser();"> 
				<g:message	code="jssh.add.servers.default" default="Add jsshUser" />
		</a>
		
	<a href="#list-jsshuser" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="" /></a>

	<div id="list-jsshuser" class="content scaffold-list" role="main">
		<h1>
			<g:message code="default.list.label" args="[entityName]" />
		</h1>
		<g:if test="${flash.message}">
			<div class="message" role="status">
				${flash.message}
			</div>
		</g:if>
		


	
		<div class="row">
			<div class="col-md-2">
				<g:sortableColumn property="username"
					title="${message(code: 'jsshuser.username.label', default: 'Jssh UserName')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-1">
				<g:sortableColumn property="permissions"
					title="${message(code: 'jsshuser.permissions.label', default: 'permissions')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-1">
				<g:sortableColumn property="conlog"
					title="${message(code: 'jsshuser.conlog.label', default: 'conlog')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-2">
				<g:message code="jsshuser.sshuser.label" default="sshuser" />
			</div>
			<div class="col-md-2">
				<g:message code="jsshuser.servers.label" default="servers" />
			</div>
			
			<div class="col-md-1">
				<g:message code="jsshuser.groups.label" default="groups" />
			</div>

			<div class="col-md-2">
				<g:message code="jsshuser.action.label" default="action" />
			</div>
			
		</div>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
				<div class="col-md-2">
					<g:link controller="connectSsh" action="edit"
						id="${userInstance.id}" params="${[table: 'jsshUser'] }">
						${fieldValue(bean: userInstance, field: "username")}
					</g:link>
				</div>
				<div class="col-md-1">
					${fieldValue(bean: userInstance, field: "permissions")}
				</div>
				<div class="col-md-1">
					${fieldValue(bean: userInstance, field: "conlog")}
				</div>

				<div class="col-md-2">
					<jssh:sshList rtype="sshuser" ilist="${userInstance.sshuser}" />
				</div>

				<div class="col-md-2">
					<jssh:sshList rtype="servers" ilist="${userInstance.servers}" />
				</div>

				<div class="col-md-1">
					<jssh:sshList rtype="groups" ilist="${userInstance.groups}" />
				</div>
				
				<div class="col-md-2">
				<div class="btn">
					<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-xs btn-warning"	onclick="javascript:cloneJsshUser('${userInstance.username}');"> 
						<g:message	code="jssh.add.servers.default" default="Clone" />
					</a>
					<g:link controller="connectSsh" action="viewLogs" class="btn btn-xs btn-success"
						id="${userInstance.id}" params="${[table: 'connections'] }">
						<g:message	code="jssh.view.connection.logs.default" default="ConnLogs" />
					</g:link>
					
					<g:link controller="connectSsh" action="viewLogs" class="btn btn-xs btn-primary"
						id="${userInstance.id}" params="${[table: 'commands'] }">
						<g:message	code="jssh.view.connection.logs.default" default="CommandLogs" />
					</g:link>
					</div>
				</div>
				
				
			</div>
		</g:each>

		<div class="pagination">
			<g:paginate total="${userInstanceTotal}"
				params="${[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}" />
		</div>
	</div>
	</div>
</body>
</html>
