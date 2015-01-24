
<div class='modal-header'>
	<button type='button' class='close' data-dismiss='modal'
		aria-hidden='true'>×</button>
	<div id='myModalLabel'>
		<h3>
			${message(code: 'jssh.'+actionName+'.label', default: ''+actionName+'')}
		</h3>
	</div>
</div>

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">x</button>
	Add SSH Servers To Group
</div>

<div class="modal-body">
	<div class='row'>

		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.select.group.name.label" default="Select Group" />
				</label>
				<g:select name="groupId" id="mySelect" from="${serverList}" optionKey="id" optionValue="name" 
				required="required" 
				 class="one-to-one" noSelection="['':'Choose Group']"/>
			</div>
		</div>

		<div class='col-sm-4'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.new.group.name.label" default="Add Server" />
				</label>
				<g:render template="/admin/autoServer" />
				
				<div id="selectedValues">
				</div>
			</div>
		</div>

	</div>
</div>
<div class="modal-footer">
	<g:submitButton name="something" class="btn btn-primary" id="submitIt"
		onClick="sendServers();closeModal();" value="Add Group" />
</div>
<div id="invitecontainer" style="display:none;">
		<g:render template="/admin/inviteContainer"/>
	</div>