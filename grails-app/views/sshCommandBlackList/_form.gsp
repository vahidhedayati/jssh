

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'command', 'error')} required">
	<label for="command">
		<g:message code="jssh.command.label" default="command" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="command" required="" value="${uiterator?.command}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshuser', 'error')}">
	<label for="sshuser">
		<g:message code="jssh.jsshuser.label" default="sshuser" />
	</label>
	
	<g:select  name="sshuser" from="${grails.plugin.jssh.SshUser.list()}" optionKey="id" optionValue="username"
	value="${uiterator?.sshuser*.id}"  class="many-to-one" />
</div>

