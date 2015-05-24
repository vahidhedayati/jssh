<g:if test="${loadStyle.toString().equals('true') }">
	<g:render template="/assets" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}" />
</g:if>

<div id="returnAnswer"></div>
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/jsshadmin/modal" />
</div>



<div id="adminConfirmation"></div>
<nav id="Navbar" class="navbar navbar-inverse" role="navigation">

<g:render template="/connectSsh/homeButton" />
			
	<ul class="nav navbar-nav">
			<li>
				<g:link controller="connectSsh" action="siteAdmin" params="${[lookup:'jsshUser', loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					Jssh Users
				</g:link>
			</li>
			
			<li>
				<g:link  controller="connectSsh" action="siteAdmin" params="${[lookup:'sshUser',loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					SSH Users
				</g:link>
			</li>
			
	
			<li>
				<g:link  controller="connectSsh" action="siteAdmin" params="${[lookup:'sshServers', loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					SSH Servers
				</g:link>
			</li>
			
			<g:render template="/jsshadmin/adminModalMenu" />
			
			
	</ul>
</nav>


<g:javascript>
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
	var jsshUser="${session?.jsshuser}";
	
	function getUser() {
		return jsshUser;
	}
</g:javascript>
