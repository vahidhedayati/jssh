		<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'jssh.'+template+'.label', default: ''+template+'')} for ${username} </h3>

</div>
</div>
	<div class="modal-body">
	<div class="container">
		<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'addRewriteDetails']" 
	update="inviteConfirmation"  
	onComplete="closeInviteModal();verifyCommandValue('${command}'); loadList('${sshUserId}', 'rewrite');"
	>

	<input type="hidden" name="username" value="${username}">
	<input type="hidden" name="sshUserId" value="${sshUserId}">
	<div class='row'>
	<div class='col-sm-5'>
		<div class='form-group'>
			<label for="hostname">
				<g:message code="jssh.new.command.label" default="command" />
			</label>
			<g:textField name="command" value="${command}" />
		</div>
	</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-3'>
			<div class='form-group'>
				<label for="rewrite">
				<g:message code="jssh.command.replacement.label" default="Replace it With" />
				</label>
				<g:textField name="replacement" />
			</div>
		</div>
	
	<div class='row'>
		<div class='col-sm-5'>
		<div class='form-group'>
			<label for="submit">
			</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'jssh.add.rewrite.label', default: 'Add Rewrite')}" />
			</div>
		</div>
		</div>
		

	</g:formRemote>	
	</div>
	</div>
	