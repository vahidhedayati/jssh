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
	$.get("/"+getApp()+"/connectSsh/addHostName?username="+backuser+"&hostName="+newhost+"&groupId="+groupId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
	});
	$('#invitecontainer').show();
}

function loadGroup(value) {
 	$.get("/"+getApp()+"/connectSsh/loadServers?username="+backuser+"&gId="+value,function(data){
		$('#selectedValues').hide().html(data).fadeIn('slow');
	});
}

function autoBlackList(user,cmd,sshId) {
	$.get("/"+getApp()+"/connectSsh/autoBlackList?username="+backuser+"&cmd="+cmd+"&sshId="+sshId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
		loadList(sshId, 'blacklist');
	});
	$('#invitecontainer').show();
}


function autoRewriteList(user,cmd,sshId) {
	$.get("/"+getApp()+"/connectSsh/autoRewriteList?username="+backuser+"&cmd="+cmd+"&sshUserId="+sshId,function(data){
		$('#inviteUserContainer').hide().html(data).fadeIn('slow');
	});
	$('#invitecontainer').show();
}
	
function loadList(value, ltype) {
 	$.get("/"+getApp()+"/connectSsh/loadList?username="+backuser+"&listType="+ltype+"&sshId="+value,function(data){
		$('#selectedValues').hide().html(data).fadeIn('slow');
	});
}


function connectGroup() { 
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=connectGroup",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}

function addGroup() {
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=addGroup",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}

function addServer() {
	$.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=addServers",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}

function addSshUser() {
	 $.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=addSshUser",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}	

function addSshUserBlackList() {
	 $.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=addBlackList",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
		//verifyCommandValue(data)
	});
	$('#adminsTemplateContainer').show();
}	

function addSshUserRewrite() {
	 $.get("/"+getApp()+"/connectSsh/loadGroup?username="+backuser+"&template=addRewrite",function(data){
		$('#adminContainer').hide().html(data).fadeIn('slow');
	});
	$('#adminsTemplateContainer').show();
}	


