
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+actionName+'.label', default: ''+actionName+'')}
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
	<div class='col-sm-5'>
		<div class='form-group'>
			<label for="hostname">
				<g:message code="jssh.new.sshuser.label" default="SSH Username" />
			</label>
	
			<g:render template="/admin/autoSshUser" />
		</div>
	</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-3'>
			<div class='form-group'>
				<label for="IP">
				<g:message code="jssh.new.sshKey.label" default="SSH Key File" />
				</label>
				<g:textField name="sshKey" />
			</div>
		</div>
	
	
</div></div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="ADD SSH User" />
</div>
</g:formRemote>

<div id="invitecontainer" style="display:none;">
		<g:render template="/admin/inviteContainer"/>
	</div>