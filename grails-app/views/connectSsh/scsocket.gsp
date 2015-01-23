<!DOCTYPE html>
<html>
<g:render template="/connectSsh/head"/>
<body>

<div class="container">
	<div id="pageHeader" class="page-header">
		<g:message code="jssh.scsocket.label" args="[entityName]"  default="Socket Client/Server Connect"/>
	</div>
<div class="container">
<g:render template="/connectSsh/navbar"/>

<div class="btn btn-block"><h5>Socket client/Server SSH Configuration/command</h5></div>

		<g:form	name="urlParams" class="form-horizontal" method="post" action="process">
	<g:render template="/connectSsh/connectBox"/>
	<input type="hidden" name="view" value="scsocketconnect">
	<div class='row'>
		<div class='col-sm-5'>
		<div class='form-group'>
			<label for="submit">
			</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'jssh.connect.label', default: 'Connect')}" />
			</div>
		</div>
		</div>
		

	</g:form>
	</div>
	</div>
</body>
</html>