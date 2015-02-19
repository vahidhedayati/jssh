
		
<li>
  <div class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#">
      
      <i class="glyphicon glyphicon-cog"
			title="Admin menu"></i><b class="caret"></b>
	</a>
	 <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
	
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
		
		</ul>
	</div>
	</li>
