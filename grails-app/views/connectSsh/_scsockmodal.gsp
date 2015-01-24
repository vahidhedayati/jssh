
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/admin/admin" />
</div>


<ul class="nav-pills pull-right navbar-nav nav">
	<li class="dropdown"><a href="#" data-toggle="dropdown"
		class="dropdown-toggle"> <i class="glyphicon glyphicon-cog"
			title="Admin menu"></i><b class="caret"></b>
	</a>

		<ul class="dropdown-menu">
			<li class="dopdown-menu"><a tabindex="-1" href="#"><b>Admin
						Options</b></a></li>
			<li class="divider"></li>
			<g:if env="development">
				<li class=""><a href="${createLink(uri: '/dbconsole')}"> <i
						class="icon-dashboard"></i> <g:message
							code="default.dbconsole.label" default="DB Console" />
				</a></li>
				<li class="divider"></li>
			</g:if>

			<li><a data-toggle="modal" href="#masterAdminContainer"
				onclick="javascript:addGroup();"> <g:message
						code="jssh.add.group.default" default="Add SSH Group" />
			</a></li>
			<li><a data-toggle="modal" href="#masterAdminContainer"
				onclick="javascript:addServer();"> <g:message
						code="jssh.add.servers.default" default="Add Servers To Group" />
			</a></li>

		</ul></li>
</ul>



<g:javascript>
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}

	function addGroup() {
		$.get("/"+getApp()+"/connectSsh/addGroup",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}

	function addServer() {
		$.get("/"+getApp()+"/connectSsh/addServers?username=${backuser}",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}
</g:javascript>