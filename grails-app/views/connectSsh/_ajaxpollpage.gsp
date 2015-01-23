
<div class="container">

<g:render template="/connectSsh/navbar"/>


<div class="btn btn-block"><h5>Ajax Poll SSH Configuration/command</h5></div>


 <div  class="form-group form-condensed">
 
	<g:form method="post" action="process">

	<g:render template="/connectSsh/connectBox"/>

	<div class='row'>
		<div class='col-sm-12'>
			<g:actionSubmit class="btn btn-primary" value="process"/>
		</div>
	</div>
	
	</g:form>
</div>
</div>

	
