
<div class="container">
	<div class="btn btn-danger"><g:link action="socket">Socket method</g:link></div>
	<g:form method="post" action="process">
	<g:textField name="username" placeholder="Username?"/>
	<g:passwordField name="password" placeholder="password?"/>
	<g:textField name="hostname" placeholder="hostname?"/>
	<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
	<g:actionSubmit class="btn btn-primary" value="process"/>
	</g:form>
</div>
