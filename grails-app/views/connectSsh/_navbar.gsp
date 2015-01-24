<div id="adminConfirmation"></div>
<nav id="Navbar" class="navbar navbar-inverse" role="navigation">
	<ul class="nav navbar-nav">
		<g:each in="${menuMap }" var="cmenu">
			<li <g:if test="${actionName == cmenu.key}">class="active"</g:if>>
				<g:link action="${cmenu.key}">
					${cmenu.value}
				</g:link>
			</li>
		</g:each>
	</ul>
</nav>
