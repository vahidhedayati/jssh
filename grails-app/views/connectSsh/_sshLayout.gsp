<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets" />
</g:if>
<g:else>
	<g:render template="/resources" />
</g:else>
<g:if test="${((!hideSendBlock) || (!hideSendBlock.equals('YES')))}">

	<div class="pull-right btn btn-default">
		<a id="sendCommand${divId}">SHOW REMOTE EXECUTOR</a>
	</div>
	<g:javascript>
		toggleBlock('#sendCommand${divId}','#mySshBox${divId}','REMOTE EXECUTOR');
	</g:javascript>
</g:if>


<g:if test="${((!hideConsoleMenu) || (!hideConsoleMenu.equals('YES')))}">

	<div class="pull-right btn btn-default">
		<a id="consoleMenu${divId}">HIDE CONSOLE MENU</a>
	</div>
</g:if>

<g:javascript>
		toggleBlock('#consoleMenu${divId}','#consoleMenuBar${divId}','CONSOLE MENU');
	</g:javascript>
<g:if test="${((!hideSendBlock) || (!hideSendBlock.equals('YES')))}">
	<div style="clear: both;"></div>
	<div id="mySshBox${divId}" style="display: none;">
		<div class="form-group">
			<label for="execute" class="col-sm-1 control-label">Execute:</label>
			<div class="col-sm-10">
				<div id="contact-area">
					<textarea cols="20" rows="1" id="textMessage${divId}"
						name="message"></textarea>
					<button class="btn btn-primary sender"
						onclick="sendMessage${divId}();">Send</button>
				</div>
			</div>
		</div>
	</div>
</g:if>



<div style="clear: both"></div>
<div class="logconsolebar" id="consoleMenuBar${divId}">
	<g:if
		test="${((!hideConsoleMenu) || (!hideConsoleMenu.equals('YES')))}">
		<g:if
			test="${((!hideWhatsRunning) || (!hideWhatsRunning.equals('YES')))}">
			<div class="btn btn-primary btn-xs"
				title="${hostname } ssh connection">
				<b>
					${hostname }: running <span id="whatCommand${divId}"></span>
				</b> | CONN_COUNT: <span id="connectionCount${divId}"></span>
			</div>
		</g:if>


		<g:if
			test="${((!hideDiscoButton) || (!hideDiscoButton.equals('YES')))}">
			<btn class="btn btn-danger btn-xs"
				onclick="closeConnection${divId}()" title="Close SSH Connection">Close
			connection</btn>
		</g:if>

		<g:if
			test="${((!hideNewShellButton) || (!hideNewShellButton.equals('YES')))}">
			<btn class="btn btn-success btn-xs" onclick="NewShell${divId}()"
				title="New Shell">New Shell</btn>
			<btn class="btn btn-primary btn-xs" onclick="CloseShell${divId}()"
				title="New Shell">Close Shell</btn>
		</g:if>

		<g:if
			test="${((!hidePauseControl) || (!hidePauseControl.equals('YES')))}">
			<btn class="btn btn-warning btn-xs" onclick="Pause${divId}()"
				title="Pause SSH Connection">Pause</btn>
			<btn class="btn btn-success btn-xs" onclick="Resume${divId}()"
				title="Resume SSH Connection">Resume</btn>
		</g:if>

		<g:if
			test="${((!hideSessionCtrl) || (!hideSessionCtrl.equals('YES')))}">
			<btn class="btn btn-danger btn-xs " onclick="NewSess${divId}()"
				title="New SSH Login per command sent">New CONN_PT</btn>
			<btn class="btn btn-primary btn-xs" onclick="SameSess${divId}()"
				title="Same SSH Login per command sent">Same CONN_PT</btn>
		</g:if>
	</g:if>
	<g:else>
		<g:if
			test="${((!hideWhatsRunning) || (!hideWhatsRunning.equals('YES')))}">
			<div class="btn btn-primary btn-xs"
				title="${hostname } ssh connection">
				<b>
					${hostname }: running <span id="whatCommand${divId}"></span>
				</b> | CONN_COUNT: <span id="connectionCount${divId}"></span>
			</div>
		</g:if>
		<g:if
			test="${((!hideDiscoButton) || (!hideDiscoButton.equals('YES')))}">
			<btn class="btn btn-danger btn-xs"
				onclick="closeConnection${divId}()" title="Close SSH Connection">Close
			connection</btn>
		</g:if>
		<g:if
			test="${((!hidePauseControl) || (!hidePauseControl.equals('YES')))}">
			<btn class="btn btn-warning btn-xs" onclick="Pause${divId}()"
				title="Pause SSH Connection">Pause</btn>
			<btn class="btn btn-success btn-xs" onclick="Resume${divId}()"
				title="Resume SSH Connection">Resume</btn>
		</g:if>
	</g:else>
</div>

<div id="mySshOut${divId}">
	<g:if test="${divId}">
		<pre class="logconsole-sm" id="messagesTextarea${divId}">
		</pre>
	</g:if>
	<g:else>
		<pre class="logconsole-lg" id="messagesTextarea${divId}">
		</pre>
	</g:else>
</div>

<g:render template="/connectSsh/socketjs" />