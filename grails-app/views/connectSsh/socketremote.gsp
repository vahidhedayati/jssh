<!DOCTYPE html>
<html>
<g:render template="/connectSsh/head" />
<body>

	<div class="container">
		<div id="pageHeader" class="page-header">
			<g:message code="jssh.wssocketremoteForm.label" args="[entityName]"
				default="Websocket Socket RemoteForm Connect" />
		</div>
		<div class="container">
			<g:render template="/connectSsh/navbar" />

			<div class="btn btn-block">
				<h5>WebSocket SSH in remoteForm (update a Div)</h5>
			</div>
			<div class="form-group form-condensed">

				<g:formRemote name="socketConnection"
					url="[controller:'connectSsh', action:'process']" update="myDiv">

					<input type="hidden" name="remoteForm" value="true">
					<input type="hidden" name="view" value="socketprocess">
					<g:render template="/connectSsh/connectBox" />

					<div class='row'>
						<div class='col-sm-12'>
							<g:submitToRemote class="btn btn-primary"
								url="[controller:'connectSsh', action:'process']" update="myDiv"
								value="Send" />
						</div>
					</div>
				</g:formRemote>
			</div>
		</div>


	</div>
	<div id="myDiv"></div>

</body>
</html>