
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
	url="[controller:'connectSsh', action:'addjsshUser']" 
	update="returnAnswer"  
	onComplete="closeModal();"
	>
<div class="modal-body">


	<div class='row'>
	
		
		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="FriendlyName">
				<g:message code="jssh.new.jsshUser.label" default="jssh Username" />
				</label>
				<g:textField name="username" />
			</div>
			</div>
			
	<div class='col-sm-4'>
		<div class='form-group'>
			<label for="hostname">
				<g:message code="jssh.new.permissions.label" default="Permissions" />
			</label>
	
			<g:select  name="permissions" from="${grails.plugin.jssh.JsshPermissions.list()}"
	value="" required="required" class="many-to-one"   optionKey="id" optionValue="name"/>
			
			
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