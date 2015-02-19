
<div id="returnAnswer"></div>
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/jsshadmin/admin" />
</div>


<ul class="nav">
	<g:render template="/connectSsh/adminModal" />
</ul>



<g:javascript>
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
	var backuser="${backuser}";
</g:javascript>
