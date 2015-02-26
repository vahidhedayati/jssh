<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'jsshuser.connection.logs.label', default: 'Jssh User Connection Logs')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<g:render template="/connectSsh/jsshAdmin"
	model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }" />
</head>
<body>
<div class="container">
		
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
				<g:sortableColumn property="jsshUser"
					title="${message(code: 'jsshuser.username.label', default: 'Jssh UserName')}"
					params="[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-2">
				<g:sortableColumn property="sshUser"
					title="${message(code: 'jsshuser.sshUser.label', default: 'sshUser')}"
					params="[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-2">
				<g:sortableColumn property="hostName"
					title="${message(code: 'jsshuser.hostName.label', default: 'hostName')}"
					params="[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
							<div class="col-md-1">
				<g:sortableColumn property="port"
					title="${message(code: 'jsshuser.port.label', default: 'port')}"
					params="[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
				<div class="col-md-2">
				<g:sortableColumn property="dateCreated"
					title="${message(code: 'jsshuser.dateCreated.label', default: 'dateCreated')}"
					params="[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			
		</div>
		<g:each in="${resultSet}" status="i" var="userInstance">
			<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
				
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "jsshUser")}
				</div>
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "sshUser")}
				</div>
				
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "hostName")}
				</div>
				<div class="col-md-1">
						${fieldValue(bean: userInstance, field: "port")}
					
				</div>
				<div class="col-md-2">
					${fieldValue(bean: userInstance, field: "dateCreated")}
				</div>
			</div>
		</g:each>

		<div class="pagination">
			<g:paginate total="${resultCount}"
				params="${[id:id, table:table, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}" />
		</div>
	</div>
	</div>
</body>
</html>
