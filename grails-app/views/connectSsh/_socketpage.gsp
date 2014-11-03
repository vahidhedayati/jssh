
<div class="container">

<nav id="Navbar" class="navbar navbar-inverse" role="navigation"> 
	<ul class="nav navbar-nav">
		<li><g:link action="ajaxpoll">Ajax poll</g:link></li>
		<li class="active"><a href="#">Socket method</a></li>
		<li><g:link action="socketremote">RemoteForm Websocket</g:link></li>
	</ul>
</nav>	


<div class="btn btn-block"><h5>Default WebSocket SSH Configuration/command</h5></div>
	<div  class="form-group form-condensed">
	
	<g:form method="post" action="socketprocess">
	
		<g:if test="${((!hideAuthBlock) || (!hideAuthBlock.equals('YES')))}">	

			<div class="pull-right btn btn-default"><a id="authCtrl">SHOW AUTHENTICATION</a></div>
<g:javascript>
toggleBlock('#authCtrl','.authList','AUTHENTICATION');
</g:javascript>

			<div style="clear:both;"></div>
			<div class="authList" id="authList" style="display: none;">	
			<div class='row'>
				<div class='col-sm-4'>
				<div class='form-group'>
	 				<label for="username">SSH Username:</label>
					<g:textField name="username" placeholder="Username?" class="form-control form-fixer"/>
				</div>
				</div>
		
				<div class='col-sm-4'>
				<div class='form-group'>
					<label for="password">SSH Password:</label>
	 				<g:passwordField name="password" placeholder="password?" class="form-control form-fixer"/>
				</div>
				</div>
		
				<div class='col-sm-4'>
				<div class='form-group'>
					<label for="hostname">SSH Hostname:</label>
					<g:textField name="hostname" placeholder="hostname?" class="form-control form-fixer"/>
				</div>
				</div>
			</div></div>
		</g:if>
		
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
				<g:actionSubmit class="btn btn-primary" value="socketprocess"/>
			</div>
		</div>	
	</g:form>
	</div>
</div>
