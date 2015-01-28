
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
	url="[controller:'connectSsh', action:'addGroupDetails']" 
	update="inviteConfirmation"  
	onComplete="closeModal();"
	>
	
<div class="modal-body">

	<input type="hidden" name="username" value="${username}">
	
	<div class='row'>

		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.new.group.name.label" default="New Group" />
				</label>
				<g:textField id="groupName" name="name" />
			</div>
		</div>

	</div>
</div>
<div class="modal-footer">
	<g:submitButton name="something" class="btn btn-primary" id="submitIt"
		 value="Add Group" />
</div>
</g:formRemote>