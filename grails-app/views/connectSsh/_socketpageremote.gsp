

<div class="container">
	<div class="btn btn-danger"><g:link action="ajaxpoll">Ajax poll</g:link></div>
	<g:formRemote name="socketConnection" 
		url="[controller:'connectSsh', action:'socketprocess']"
        update="${divId }"
    >
	<g:textField name="username" placeholder="Username?"/>
	<g:passwordField name="password" placeholder="password?"/>
	<g:textField name="hostname" placeholder="hostname?"/>
	<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
	<g:submitToRemote class="btn btn-primary" url="[controller:'connectSsh', action:'socketprocess']" update="${divid }"  value="Send" />
	</g:formRemote>
</div>
