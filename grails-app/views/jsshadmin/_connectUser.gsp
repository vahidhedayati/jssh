<g:if test="${loadStyle.toString().equals('true') }"> 
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}" />
</g:if>
<g:else>
	<g:render template="/resources" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}"/>
</g:else>
</g:if>

<div class="container">
<g:if test="${serverList}">
	<g:form name="groupConn" class="form-horizontal" controller="connectSsh" action="groupConnection"	>
<div class="modal-body">
 	<g:hiddenField name="jsshUsername" value="${username }" />
 	<g:hiddenField name="hideWhatsRunning" value="${hideWhatsRunning }" />
  	<g:hiddenField name="hideDiscoButton" value="${hideDiscoButton }" />
   	<g:hiddenField name="hidePauseControl" value="${hidePauseControl }" />
    <g:hiddenField name="hideSessionCtrl" value="${hideSessionCtrl }" />
    <g:hiddenField name="hideConsoleMenu" value="${hideConsoleMenu }" />
    <g:hiddenField name="hideSendBlock" value="${hideSendBlock }" />
    <g:hiddenField name="hideBroadCastBlock" value="${hideBroadCastBlock }" />
	<div class='row'>
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="groupname"> <g:message
						code="jssh.select.group.name.label" default="Select Group" />
				</label>
				<g:select name="groupId" id="mySelect" from="${serverList}" optionKey="id" optionValue="name" 
				required="required" class="one-to-one" noSelection="['':'Choose Group']"/>				 
				 
				 <g:if test="${!userCommand }">
					 <g:textField id="userCommand" name="userCommand" placeHolder="Global command to run on server group?"/>
				 </g:if>
				 <g:else>
					 <g:hiddenField id="userCommand" name="userCommand" value="${userCommand }"/>
				 </g:else>			 
			</div>
		</div>
	</div>
	
</div>

<div class="modal-footer">
	<g:submitButton  name="something" class="btn btn-primary"  value="SSH Connect" />
</div>
</g:form>
</g:if>
<g:else>
No Connections have been configured as yet
</g:else>
</div>