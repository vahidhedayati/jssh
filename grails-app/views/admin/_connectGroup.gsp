
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
	<g:form name="groupConn" class="form-horizontal" action="groupConnection"	>
<div class="modal-body">
<input type="hidden" name="jsshUsername" value="${username }">
	<div class='row'>
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.select.group.name.label" default="Select Group" />
				</label>
				<g:select name="groupId" id="mySelect" from="${serverList}" optionKey="id" optionValue="name" 
				required="required" 
				 class="one-to-one" noSelection="['':'Choose Group']"/>
				 
				 <g:select name="userId" id="mySelect" from="${sshUserList}" optionKey="id" optionValue="username" 
				required="required" 
				 class="one-to-one" noSelection="['':'Choose SSh UserName']"/>
				 
				 
				 <g:textField id="userCommand" name="userCommand" placeHolder="Global command to run on server group?"/>
				 
			</div>
		</div>
	</div>
	
</div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="SSH Connect" />
</div>
</g:form>
</g:if>
