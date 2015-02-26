


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'friendlyName', 'error')} required">
	<label for="friendlyName">
		<g:message code="jssh.friendlyName.label" default="friendlyName" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="friendlyName" required="" value="${uiterator?.friendlyName}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'username', 'error')} required">
	<label for="name">
		<g:message code="jssh.username.label" default="username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${uiterator?.username}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshKey', 'error')} required">
	<label for="name">
		<g:message code="jssh.sshKey.label" default="sshKey" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="sshKey" required="" value="${uiterator?.sshKey}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'sshKeyPass', 'error')}">
	<label for=sshKeyPass>
		<g:message code="jssh.sshKey.label" default="sshKeyPass" />
	</label>
	<g:textField name="sshKeyPass"  value="${uiterator?.sshKeyPass}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'servers', 'error')}">
	<label for="servers">
		<g:message code="jssh.servers.label" default="servers" />
	</label>

	<g:select  name="servers" from="${grails.plugin.jssh.SshServers.list()}" optionKey="id" optionValue="hostName"
	value="${uiterator?.servers*.id}"  class="many-to-one" />
</div>


<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'blacklist', 'error')}">
	<label for="blacklist">
		<g:message code="jssh.blacklist.label" default="blacklist" />
	</label>
	
	<g:select  name="blacklist" from="${grails.plugin.jssh.SshCommandBlackList.list()}" optionKey="id" optionValue="command"
	value="${uiterator?.blacklist*.id}"  class="many-to-one" />
</div>

<div class="fieldcontain ${hasErrors(bean: uiterator, field: 'rewrite', 'error')}">
	<label for="rewrite">
		<g:message code="jssh.rewrite.label" default="rewrite" />
	</label>
	
	<g:select  name="rewrite" from="${grails.plugin.jssh.SshCommandRewrite.list()}" optionKey="id" optionValue="command"
	value="${uiterator?.rewrite*.id}"  class="many-to-one" />
</div>
