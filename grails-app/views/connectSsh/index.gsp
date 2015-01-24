<!DOCTYPE html>
<html>
<g:render template="/connectSsh/head" />
<body>

	<div class="container">
		<div id="pageHeader" class="page-header">
			<g:message code="jssh.wssocket.label" args="[entityName]"
				default="Websocket Socket Connect" />
		</div>
		<div class="container">
			<g:render template="/connectSsh/navbar" />

			<div class="btn btn-block">
				<h5>Default WebSocket SSH Configuration/command</h5>
			</div>
			<div class="form-group form-condensed">

				<g:form name="go" action="process">

					<g:render template="/connectSsh/connectBox" />
					<input type="hidden" name="view" value="socketprocess">
					<div class='row'>
						<div class='col-sm-12'>
							<g:submitButton name="send" class="btn btn-primary"
								value="socketprocess" />
						</div>
					</div>
				</g:form>
			</div>
		</div>
	</div>
</body>
</html>