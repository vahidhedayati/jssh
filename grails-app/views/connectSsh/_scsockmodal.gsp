<div id="returnAnswer"></div>
<div id="adminsTemplateContainer" style="display: none;">
	<g:render template="/jsshadmin/admin" />
</div>


<ul class="nav-pills pull-center navbar-nav nav">
	<li class="dropdown"><a href="#" data-toggle="btn btn-default dropdown-toggle  btn-sm"
		class="dropdown-toggle"> <i class="glyphicon glyphicon-cog"
			title="Admin menu"></i><b class="caret"></b>
	</a>

		<ul class="dropdown-menu">
			<li ><a tabindex="-1" href="#"><b>Admin
						Options</b></a></li>
			<li class="divider"></li>
			<g:if env="development">
				<li ><a href="${createLink(uri: '/dbconsole')}"> <i
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
				onclick="javascript:addSshUserBlackList();"> <g:message
						code="jssh.add.ssh.user.blacklist.default" default="SSHUser CMD BlackList" />
			</a></li>
			
			
			<li><a data-toggle="modal" href="#masterAdminContainer"
				onclick="javascript:addSshUserRewrite();"> <g:message
						code="jssh.add.ssh.user.command.rewrite.default" default="SSHUser CMD Rewrite" />
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
	
	function autoBlackList(user,cmd,sshId) {
		$.get("/"+getApp()+"/connectSsh/autoBlackList?username=${backuser}&cmd="+cmd+"&sshId="+sshId,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
			loadList(sshId, 'blacklist');
		});
		$('#invitecontainer').show();
	}
	
	
	function autoRewriteList(user,cmd,sshId) {
		$.get("/"+getApp()+"/connectSsh/autoRewriteList?username=${backuser}&cmd="+cmd+"&sshUserId="+sshId,function(data){
			$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		});
		$('#invitecontainer').show();
	}
		
	function loadList(value, ltype) {
	 	$.get("/"+getApp()+"/connectSsh/loadList?username=${backuser}&listType="+ltype+"&sshId="+value,function(data){
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
		$.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=addGroup",function(data){
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
	
	function addSshUserBlackList() {
		 $.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=addBlackList",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
			//verifyCommandValue(data)
		});
		$('#adminsTemplateContainer').show();
	}	
	
	function addSshUserRewrite() {
		 $.get("/"+getApp()+"/connectSsh/loadGroup?username=${backuser}&template=addRewrite",function(data){
			$('#adminContainer').hide().html(data).fadeIn('slow');
		});
		$('#adminsTemplateContainer').show();
	}	
	
	
	
</g:javascript>