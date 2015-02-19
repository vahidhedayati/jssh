

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'hostName', 'error')} required">
	<label for="hostName">
		<g:message code="jssh.username.label" default="hostName" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="hostName" required="" value="${uiterator?.hostName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'ipAddress', 'error')}">
	<label for="name">
		<g:message code="jssh.ipAddress.label" default="ipAddress" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="ipAddress"  value="${uiterator?.ipAddress}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshPort', 'error')}">
	<label for="sshPort">
		<g:message code="jssh.sshPort.label" default="sshPort" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="sshPort"  value="${uiterator?.sshPort ?: '22'}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshuser', 'error')}">
	<label for="name">
		<g:message code="jssh.sshuser.label" default="sshuser" />
	</label>
	
	<g:select  name="sshuser" from="${grails.plugin.jssh.SshUser.list()}" optionKey="id" optionValue="username"
	value="${uiterator?.sshuser*.id}"  class="many-to-one" />
</div>