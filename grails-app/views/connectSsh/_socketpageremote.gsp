
<div class="container">

<g:render template="/connectSsh/navbar"/>

	<div class="btn btn-block"><h5>WebSocket RemoteForm SSH Configuration/command</h5>
	<h6>(not working within plugin - an idea on howto)</h6></div>
	<div  class="form-group form-condensed">

		<g:formRemote name="socketConnection" 
			url="[controller:'connectSsh', action:'socketprocess']"
       	 	update="myDiv"
    	>
    	
    	<input type="hidden" name="remoteForm" value="true">
    	
   		<g:render template="/connectSsh/connectBox"/>
   
		<div class='row'>
			<div class='col-sm-12'>		
				<g:submitToRemote class="btn btn-primary" url="[controller:'connectSsh', action:'socketprocess']" update="myDiv"  value="Send" />
			</div>
		</div>	
	</g:formRemote>
</div>
</div>

<div id="myDiv">
<!--  SSH OUTPUT FROM REMOTE FORM SHOULD END HERE -->
</div>
