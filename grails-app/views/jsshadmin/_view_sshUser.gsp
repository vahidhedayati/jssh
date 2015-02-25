<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'jsshuser.label', default: 'Jssh SSHUser')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
<g:render template="/connectSsh/jsshAdmin"
	model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle] }" />
</head>
<body>

	<g:select name="cUser" id="myUserSelection" from="${grails.plugin.jssh.JsshUser.list()}" 
		optionKey="username" optionValue="username" 
	 	class="one-to-one" noSelection="['':'Choose User']" value="${session.jsshuser }" 
	 />
	
	
	<a data-toggle="modal" href="#masterAdminContainer"
		onclick="javascript:addGroup();"  class="btn btn-primary"> <g:message
		code="jssh.add.group.default" default="Add SSH Group" />
	</a>
			
	<a data-toggle="modal" href="#masterAdminContainer" onclick="javascript:addServer();" class="btn btn-warning"> 
	<g:message code="jssh.add.ssh.user.default" default="Add SSH Server"/></a>
	
	
	<a data-toggle="modal" href="#masterAdminContainer"
		onclick="javascript:addSshUser();"  class="btn btn-success"> <g:message
		code="jssh.add.ssh.user.default" default="Add SSH User" />
	</a>
	
	<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-warning"	onclick="javascript:addSshUserBlackList();"> 
				<g:message	code="jssh.add.blacklist.default" default="Add BlackList Command" />
		</a>
		
	<a data-toggle="modal" href="#masterAdminContainer" class="btn btn-success"	onclick="javascript:addSshUserRewrite();"> 
				<g:message	code="jssh.add.rewrite.default" default="Add Rewrite Command" />
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
					title="${message(code: 'jsshuser.username.label', default: 'username')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-1">
				<g:sortableColumn property="sshKey"
					title="${message(code: 'jsshuser.sshKey.label', default: 'sshKey')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-1">
				<g:sortableColumn property="sshKeyPass"
					title="${message(code: 'jsshuser.sshKeyPass.label', default: 'sshKeyPass')}"
					params="[id:id, lookup:lookup, loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]" />
			</div>
			<div class="col-md-3">
				<g:message code="jsshuser.servers.label" default="servers" />
			</div>
			<div class="col-md-2">
				<g:message code="jsshuser.blacklist.label" default="blacklist" />
			</div>
			<div class="col-md-2">
				<g:message code="jsshuser.rewrite.label" default="rewrite" />
			</div>
		</div>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<div class="row ${(i % 2) == 0 ? 'even' : 'odd'}">
				<div class="col-md-2">
					<g:link controller="connectSsh" action="edit"
						id="${userInstance.id}" params="${[table: 'sshUser'] }">
						${fieldValue(bean: userInstance, field: "username")}
					</g:link>
					:
					${userInstance.friendlyName }
				</div>
				<div class="col-md-1">
					${fieldValue(bean: userInstance, field: "sshKey")}
				</div>
				<div class="col-md-1">
					${fieldValue(bean: userInstance, field: "sshKeyPass")}
				</div>

				<div class="col-md-3">
					<jssh:sshList rtype="servers" ilist="${userInstance.servers}" />
				</div>
				<div class="col-md-2">
					<jssh:sshList rtype="blacklist" ilist="${userInstance.blacklist}" />
				</div>

				<div class="col-md-2">
					<jssh:sshList rtype="rewrite" ilist="${userInstance.rewrite}" />
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
