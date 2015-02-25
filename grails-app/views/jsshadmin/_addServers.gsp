
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+template+'.label', default: ''+template+'')} for ${username}
		</h3>
	</div>
</div>
<g:if test="${serverList}">
	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'connectSsh', action:'addGroupServers']" 
	update="returnAnswer"  
	onComplete="closeModal();"
	>
<div class="modal-body">

	<div class='row'>
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.select.group.name.label" default="Select Group" />
				</label>
				<g:select name="groupId" id="mySelect" from="${serverList}" optionKey="id" optionValue="name" 
				required="required" 
				 class="one-to-one" noSelection="['':'Choose Group']" onChange="showServers(this.value);loadGroup(this.value);"/>
			</div>
		</div>
	</div>
	
	<div class='row' id="serverRow" style="display:none;">
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.new.group.name.label" default="Add Server" />
				</label>
				<g:render template="/jsshadmin/autoServer" />
				
				<div id="selectedValues">
				</div>
			</div>
		</div>

	</div>
</div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="Update Group" />
</div>
</g:formRemote>
</g:if>
<g:else>
No groups have been created as yet!
</g:else>
<div id="invitecontainer" style="display:none;">
		<g:render template="/jsshadmin/inviteContainer"/>
	</div>