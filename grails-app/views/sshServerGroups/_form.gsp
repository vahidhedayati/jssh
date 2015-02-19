

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="jssh.username.label" default="name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${uiterator?.name}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'user', 'error')}">
	<label for="jsshuser">
		<g:message code="jssh.jsshuser.label" default="jsshuser" />
	</label>
	
	<g:select  name="sshuser" from="${grails.plugin.jssh.JsshUser.list()}" optionKey="id" optionValue="username"
	value="${uiterator?.user*.id}"  class="many-to-one" />
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'servers', 'error')}">
	<label for="name">
		<g:message code="jssh.servers.label" default="servers" />
	</label>

	<g:select  name="servers" from="${grails.plugin.jssh.SshServers.list()}" optionKey="id" optionValue="hostName"
	value="${uiterator?.servers*.id}"  class="many-to-one" />
</div>

