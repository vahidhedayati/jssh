<g:if test="${loadStyle.toString().equals('true') }"> 
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:render template="/assets" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}" />
</g:if>
<g:else>
	<g:render template="/resources" model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery]}"/>
</g:else>
</g:if>

<div id="returnAnswer"></div>
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/jsshadmin/admin" />
</div>



<div id="adminConfirmation"></div>
<nav id="Navbar" class="navbar navbar-inverse" role="navigation">

<g:render template="/connectSsh/homeButton" />
			
	<ul class="nav navbar-nav">
			<li>
				<g:link controller="connectSsh" action="siteAdmin" params="${[lookup:'user',loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					Jssh Users
				</g:link>
			</li>
			
			<li>
				<g:link  controller="connectSsh" action="siteAdmin" params="${[lookup:'sshuser',loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					SSH Users
				</g:link>
			</li>
			
	
			<li>
				<g:link  controller="connectSsh" action="siteAdmin" params="${[lookup:'server', loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}">
					SSH Servers
				</g:link>
			</li>
			
			<g:render template="/connectSsh/adminModal" />
			
			
	</ul>
</nav>


<g:javascript>
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
	var backuser="${backuser}";
	
	function getUser() {
		return backuser;
	}
</g:javascript>
