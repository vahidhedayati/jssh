
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+actionName+'.label', default: ''+actionName+'')}
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
				 class="one-to-one" noSelection="['':'Choose Group']" onChange="loadGroup(this.value);"/>
			</div>
		</div>
	</div>
<div id="selectedValues">
				</div>
</div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="SSH Connect" />
</div>
</g:formRemote>
</g:if>
