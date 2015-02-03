
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+template+'.label', default: ''+template+'')}
		</h3>
	</div>
</div>
<g:if test="${sshUserList}">
	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'addBlackList']" 
	update="returnAnswer"  
	onComplete="closeModal();"
	>
<div class="modal-body">

	<div class='row'>
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.select.ssh.user.name.label" default="Select SSH User" />
				</label>
				<g:select name="sshuserId" id="mySelect" from="${sshUserList}" optionKey="id" optionValue="username" 
				required="required" 
				 class="one-to-one" noSelection="['':'Choose SshUser']" onChange="showServers(this.value);loadGroup(this.value);"/>
			</div>
		</div>
	</div>
	
	<div class='row' id="serverRow" style="display:none;">
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.new.blacklist.command.label" default="Add BlackList Command" />
				</label>
				<g:render template="/admin/autoBlackList" />
				
				<div id="selectedValues">
				</div>
			</div>
		</div>

	</div>
</div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="Update BlackList for SSHUSER" />
</div>
</g:formRemote>
</g:if>
<g:else>
No SSH USERS  been created as yet!
</g:else>
<div id="invitecontainer" style="display:none;">
		<g:render template="/admin/inviteContainer"/>
	</div>