
<div id="returnAnswer"></div>
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/jsshadmin/modal" />
</div>

<g:render template="/connectSsh/homeButton" />
<ul  class="nav navbar-nav">
	<g:render template="/jsshadmin/adminModalMenu" />
</ul>



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
