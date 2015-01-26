<div id="returnAnswer"></div>
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
			
			<li><a data-toggle="modal" href="#masterAdminContainer"
				onclick="javascript:addSshUser();"> <g:message
						code="jssh.add.ssh.user.default" default="Add SSH User" />
			</a></li>
			
			
			<li><a data-toggle="modal" href="#masterAdminContainer"
				onclick="javascript:connectGroup();"> <g:message
						code="jssh.connect.group.default" default="Connect To Group" />
			</a></li>
		</ul></li>
</ul>



<g:javascript>
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
	
	function addHost(user,newhost,groupId) {
		$.get("/"+getApp()+"/connectSsh/addHostName?username=${backuser}&hostName="+newhost+"&groupId="+groupId,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
	
	}
	
	function loadGroup(value) {
	 	$.get("/"+getApp()+"/connectSsh/loadServers?username=${backuser}&gId="+value,function(data){
			$('#selectedValues').hide().html(data).fadeIn('slow');
		});
	
	}
	
	function connectGroup() { 
		$.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=connectGroup",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}

	function addGroup() {
		$.get("/"+getApp()+"/connectSsh/addGroup",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}

	function addServer() {
		$.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=addServers",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}
	
	function addSshUser() {
		 $.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=addSshUser",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}	
</g:javascript>