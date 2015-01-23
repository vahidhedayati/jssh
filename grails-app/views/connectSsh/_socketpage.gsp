
<div class="container">

<g:render template="/connectSsh/navbar"/>

<div class="btn btn-block"><h5>Default WebSocket SSH Configuration/command</h5></div>
	<div  class="form-group form-condensed">
	
	<g:form method="post" action="socketprocess">
	
	<g:render template="/connectSsh/connectBox"/>
	
		<div class='row'>
			<div class='col-sm-12'>
				<g:actionSubmit class="btn btn-primary" value="socketprocess"/>
			</div>
		</div>	
	</g:form>
	</div>
</div>
