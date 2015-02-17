
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+template+'.label', default: ''+template+'')}<br/>
			Choose a group OR<br> 
			if a groups servers have different SSH credentials then define by server and leave group empty
		</h3>
	</div>
</div>

	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'adduserDetails']" 
	update="returnAnswer"  
	onComplete="closeModal();"
	>
<div class="modal-body">



<input type="hidden" name="username" value="${username}">


	<div class='row'>
	
		
		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="FriendlyName">
				<g:message code="jssh.new.friendlyName.label" default="FriendlyName" />
				</label>
				<g:textField name="friendlyName" />
			</div>
			</div>
			
	<div class='col-sm-4'>
		<div class='form-group'>
			<label for="hostname">
				<g:message code="jssh.new.sshuser.label" default="SSH_Username" />
			</label>
	
			
			<g:textField name="sshUsername" />
			
			
		</div>
	</div>
	
	
	<div class='col-sm-3'>
			<div class='form-group'>
				<label for="Group">
				<g:message code="jssh.group.binding.label" default="Group" />
				</label>
				<g:select name="groupId" noSelection="['':'Choose Group OR server below']"  from="${grails.plugin.jssh.SshServerGroups.list()}" optionKey="id"
				optionValue="name"  />
			</div>
		</div>
		
	
	</div>
	
	<div class='row'>
		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="IP">
				<g:message code="jssh.new.sshKey.label" default="SSH_KeyFILE" />
				</label>
				<g:textField name="sshKey" />
			</div>
			</div>
			
				<div class='col-sm-4'>
			<div class='form-group'>
				<label for="IP">
				<g:message code="jssh.new.sshKeyPass.label" default="SSH_KeyPass" />
				</label>
				<g:textField name="sshKeyPass" />
			</div>
			</div>
			<div class='col-sm-3'>
			<div class='form-group'>
				<label for="Server">
				<g:message code="jssh.server.binding.label" default="Server" />
				</label>
				<g:select name="serverId" from="${grails.plugin.jssh.SshServers.list()}" optionKey="id"
				optionValue="hostName"  multiple="multiple"/>
			</div>
		</div>
	</div>
	
		</div>
	
	


<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="ADD SSH User" />
</div>
</g:formRemote>

<div id="invitecontainer" style="display:none;">
		<g:render template="/jsshadmin/inviteContainer"/>
	</div>