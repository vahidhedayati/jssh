
<div class="container">

<nav id="Navbar" class="navbar navbar-inverse" role="navigation"> 
	<ul class="nav navbar-nav">
		<li><g:link action="ajaxpoll">Ajax poll</g:link></li>
		<li ><g:link action="index">Socket method</g:link></li>
		<li class="active"><a href="#">RemoteForm Websocket</a></li>
	</ul>
</nav>	

	<div class="btn btn-block"><h5>WebSocket RemoteForm SSH Configuration/command</h5>
	<h6>(not working within plugin - an idea on howto)</h6></div>
	<div  class="form-group form-condensed">

		<g:formRemote name="socketConnection" 
			url="[controller:'connectSsh', action:'socketprocess']"
       	 	update="myDiv"
    	>
    	<input type="hidden" name="remoteForm" value="true">
    	
    	<div class='row'>
			<div class='col-sm-4'>
			<div class='form-group'>
	 			<label for="username">SSH Username:</label>
				<g:textField name="username" placeholder="Username?"/>
			</div>
			</div>
			<div class='col-sm-4'>
			<div class='form-group'>
	 			<label for="password">SSH Password:</label>
				<g:passwordField name="password" placeholder="password?"/>
			</div>
			</div>
			<div class='col-sm-4'>
			<div class='form-group'>
				<label for="hostname">SSH Hostname:</label>	
				<g:textField name="hostname" placeholder="hostname?"/>
			</div>
			</div>	
		</div>
		
		<div class='row'>
			<div class='col-sm-12'>
    		<div class='form-group'>
				<label for="execcommand">Execute Command:</label>		
				<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
			</div>
			</div>
		</div>
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
