<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'ssh.Server.command.blackList.label', default: 'Jssh Command Black Listed')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<g:render template="/connectSsh/jsshAdmin"
	model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }" />
</head>
<body>

		<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-success"	onclick="javascript:addSshUserBlackList();"> 
				<g:message	code="jssh.add.servers.default" default="Add BlackList Command" />
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
				<g:sortableColumn property="command"
					title="${message(code: 'jsshuser.command.label', default: 'command')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-2">
				<g:sortableColumn property="sshuser"
					title="${message(code: 'jsshuser.sshuser.label', default: 'sshuser')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
		

		</div>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
				<div class="col-md-2">
					<g:link controller="connectSsh" action="edit"
						id="${userInstance.id}" params="${[table: 'sshCommandBlackList'] }">
						${fieldValue(bean: userInstance, field: "command")}
					</g:link>
				</div>
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "sshuser")}
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
