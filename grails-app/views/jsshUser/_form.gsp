

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'username', 'error')} required">
	<label for="name">
		<g:message code="jssh.username.label" default="username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${uiterator?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'permissions', 'error')} required">
	<label for="name">
		<g:message code="jssh.permissions.label" default="permissions" />
		<span class="required-indicator">*</span>
	</label>
	<g:select  name="permissions" from="${grails.plugin.jssh.JsshPermissions.list()}"
	value="${uiterator?.permissions*.id}" required="required" class="many-to-one"   optionKey="id" optionValue="name"/>
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshuser', 'error')}">
	<label for="name">
		<g:message code="jssh.sshuser.label" default="sshuser" />
	</label>
	
	<g:select  name="sshuser" from="${grails.plugin.jssh.SshUser.list()}" optionKey="id" optionValue="username"
	value="${uiterator?.sshuser*.id}"  class="many-to-one" />
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'servers', 'error')}">
	<label for="name">
		<g:message code="jssh.servers.label" default="servers" />
	</label>

	<g:select  name="servers" from="${grails.plugin.jssh.SshServers.list()}" optionKey="id" optionValue="hostName"
	value="${uiterator?.servers*.id}"  class="many-to-one" />
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'groups', 'error')}">
	<label for="name">
		<g:message code="jssh.groups.label" default="Groups" />
	</label>
	
	<g:select  name="groups" from="${grails.plugin.jssh.SshServerGroups.list()}" optionKey="id" optionValue="name"
	value="${uiterator?.groups*.id}" class="many-to-one" />
</div>
