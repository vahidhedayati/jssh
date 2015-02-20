<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'ssh.Server.group.label', default: 'Jssh Groups')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<g:render template="/connectSsh/jsshAdmin"
	model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }" />
</head>
<body>

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

			<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-success"
				onclick="javascript:addServer();"> <g:message
						code="jssh.add.servers.default" default="Add Servers To Group" />
			</a>
		<div class="row">
			<div class="col-md-2">
				<g:sortableColumn property="name"
					title="${message(code: 'jsshuser.name.label', default: 'name')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-2">
				<g:sortableColumn property="user"
					title="${message(code: 'jsshuser.user.label', default: 'user')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-3">
				<g:message code="jsshuser.servers.label" default="servers" />
			</div>

		</div>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
				<div class="col-md-2">
					<g:link controller="connectSsh" action="edit"
						id="${userInstance.id}" params="${[table: 'sshServerGroups'] }">
						${fieldValue(bean: userInstance, field: "name")}
					</g:link>
				</div>
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "user")}
				</div>
				<div class="col-md-3">
					<jssh:sshList rtype="servers" ilist="${userInstance.servers}" />
				</div>


			</div>
		</g:each>

		<div class="pagination">
			<g:paginate total="${userInstanceTotal}"
				params="${[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}" />
		</div>
	</div>
</body>
</html>
