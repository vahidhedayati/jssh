<!DOCTYPE html>
<html>
<g:render template="/connectSsh/head"/>
<body>

<div class="container">
	<div id="pageHeader" class="page-header">
		<g:message code="jssh.wssocketremoteForm.label" args="[entityName]"  default="Websocket Socket RemoteForm Connect"/>
	</div>
<div class="container">
<g:render template="/connectSsh/navbar"/>
	
<div class="btn btn-block"><h5>Ajax Poll SSH Configuration/command</h5></div>
	<div  class="form-group form-condensed">

	<g:form method="post" action="process">

	<g:render template="/connectSsh/connectBox"/>
<input type="hidden" name="view" value="ajaxprocess">
	<div class='row'>
		<div class='col-sm-12'>
			<g:actionSubmit class="btn btn-primary" value="process"/>
		</div>
	</div>
	
	</g:form>
</div>
</div>

</div>
</body>
</html>