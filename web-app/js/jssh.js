function toggleBlock(caller,called,calltext) {
	$(caller).click(function() {
		if($(called).is(":hidden")) {
			$(caller).html('HIDE '+calltext).fadeIn('slow');
		}else{
			$(caller).html('SHOW '+calltext).fadeIn('slow');	

		}
		$(called).slideToggle("fast");

	});
}	

function wrapIt(value) {
	return "'"+value+"'"
}



function addHost(user,newhost,groupId) {
	$.get("/"+getApp()+"/connectSsh/addHostName?username="+getUser()+"&hostName="+newhost+"&groupId="+groupId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
	});
	$('#invitecontainer').show();
}

function loadGroup(value) {
	$.get("/"+getApp()+"/connectSsh/loadServers?username="+getUser()+"&gId="+value,function(data){
		$('#selectedValues').hide().html(data).fadeIn('slow');
	});
}

function autoBlackList(user,cmd,sshId) {
	$.get("/"+getApp()+"/connectSsh/autoBlackList?username="+getUser()+"&cmd="+cmd+"&sshId="+sshId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		loadList(sshId, 'blacklist');
	});
	$('#invitecontainer').show();
}


function autoRewriteList(user,cmd,sshId) {
	$.get("/"+getApp()+"/connectSsh/autoRewriteList?username="+getUser()+"&cmd="+cmd+"&sshUserId="+sshId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
	});
	$('#invitecontainer').show();
}

function loadList(value, ltype) {
	$.get("/"+getApp()+"/connectSsh/loadList?username="+getUser()+"&listType="+ltype+"&sshId="+value,function(data){
		$('#selectedValues').hide().html(data).fadeIn('slow');
	});
}


function connectGroup() { 
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+getUser()+"&template=connectGroup",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}


function addGroup() {
	var cuser = $('#myUserSelection').val()
	if (cuser == undefined) {
		cuser = getUser()
	}
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+cuser+"&template=addGroup",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}

function addServer()  {
	var cuser = $('#myUserSelection').val()
	if (cuser == undefined) {
		cuser = getUser()
	}
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+cuser+"&template=addServers",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}


function addSshUser() {
	var cuser = $('#myUserSelection').val()
	if (cuser == undefined) {
		cuser = getUser()
	}
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+cuser+"&template=addSshUser",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}


function addJsshUser() {
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+getUser()+"&template=addJsshUser",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();

}

function cloneJsshUser(jsshUsername) {
	$.get("/"+getApp()+"/connectSsh/cloneUser?jsshUsername="+jsshUsername+"",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}

function addSshUserBlackList() {
	var cuser = $('#myUserSelection').val()
	if (cuser == undefined) {
		cuser = getUser()
	}
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+cuser+"&template=addBlackList",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
		//verifyCommandValue(data)
	});
	$('#adminsTemplateContainer').show();
}	

function addSshUserRewrite() {
	var cuser = $('#myUserSelection').val()
	if (cuser == undefined) {
		cuser = getUser()
	}
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+cuser+"&template=addRewrite",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}	


