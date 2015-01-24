
<g:if test="${((!hideAuthBlock) || (!hideAuthBlock.equals('YES')))}">

	<div class="pull-right btn btn-default">
		<a id="authCtrl">SHOW AUTHENTICATION</a>
	</div>

	<div style="clear: both;"></div>
	<g:javascript>
toggleBlock('#authCtrl','.authList','AUTHENTICATION');
</g:javascript>
	<div class="authList" style="display: none;">
		<div class='row'>
			<div class='col-sm-2'>
				<div class='form-group'>
					<label for="username">JSSH User:</label>
					<g:textField name="jsshUser" placeholder="Jssh Username"
						class="form-control form-fixer" />
				</div>
			</div>
			<div class='col-sm-2'>
				<div class='form-group'>
					<label for="username">SSH Username:</label>
					<g:textField name="username" placeholder="Username?"
						class="form-control form-fixer" />
				</div>
			</div>

			<div class='col-sm-2'>
				<div class='form-group'>
					<label for="password">SSH Pass:</label>
					<g:passwordField name="password" placeholder="password?"
						class="form-control form-fixer" />
				</div>
			</div>

			<div class='col-sm-2'>
				<div class='form-group'>
					<label for="hostname">Hostname:</label>
					<g:textField name="hostname" placeholder="localhost"
						value="localhost" class="form-control form-fixer" />
				</div>
			</div>

			<div class='col-sm-2'>
				<div class='form-group'>
					<label for="password">Port:</label>
					<g:textField name="port" placeholder="22" value="22"
						class="form-control form-fixer" />
				</div>
			</div>
		</div>
	</div>
</g:if>

<div class='row'>
	<div class='col-sm-12'>
		<div class='form-group'>
			<label for="execcommand">Execute Command:</label>
			<textarea name="userCommand" id="console"
				placeholder="Line separated commands to execute"></textarea>
		</div>
	</div>
</div>