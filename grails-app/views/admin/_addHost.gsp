		<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'jssh.'+actionName+'.label', default: ''+actionName+'')}</h3>

</div>
</div>
	
	<div class="container">
		<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'addHostDetails']" 
	update="inviteConfirmation"  
	onComplete="closeInviteModal();verifyValue('${hostName}')"
	>
	<div class='row'>
	<div class='col-sm-5'>
		<div class='form-group'>
			<label for="hostname">
				<g:message code="jssh.new.hostName.label" default="HostName" />
			</label>
			<g:textField name="hostName" value="${hostName }" />
		</div>
	</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-3'>
			<div class='form-group'>
				<label for="IP">
				<g:message code="jssh.new.ip.address.label" default="IP Address" />
				</label>
				<g:textField name="ip" />
			</div>
		</div>
	
		<div class='col-sm-3'>
			<div class='form-group'>
				<label for="Port">
				<g:message code="jssh.new.port.label" default="port" />
				</label>
				<g:textField name="port" />
			</div>
		</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-5'>
		<div class='form-group'>
			<label for="submit">
			</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'jssh.add.host.label', default: 'Add Host')}" />
			</div>
		</div>
		</div>
		

	</g:formRemote>	
	</div>