
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.clone.label', default: 'Clone')}  ${username }'s profile
		</h3>
	</div>
</div>
<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'cloneAccount']" 
	update="inviteConfirmation"  
	onComplete="closeModal();"
	>
	
<div class="modal-body">

	<input type="hidden" name="masteruser" value="${username}">
	
	<div class='row'>

		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.new.username.clone.label" default="New UserName" />
				</label>
				<g:textField id="username" name="username" />
			</div>
		</div>

	</div>
</div>
<div class="modal-footer">
	<g:submitButton name="something" class="btn btn-primary" id="submitIt"
		 value="Clone ${username }" />
</div>
</g:formRemote>