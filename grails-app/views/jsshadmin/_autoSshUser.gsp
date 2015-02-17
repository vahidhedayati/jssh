
<div id="userInputError">
	<div id="userInput">
		<input type='text' id='sshUsername' name="sshUsername" value="${sshUsername}" onClick="this.value='';resetMsg();" onchange="verifyUserValue(this.value);" />
	</div>
	<div id="response_image"></div>
</div>
<div id="return_result"></div>
<input type=hidden name="gpId" id="gpId">
<g:javascript>

	function verifyUserValue(data) {
		if (data!='') {
			$.getJSON('${createLink(controller:"connectSsh", action: "findSshUser")}?username='+data,function(e){
			var found=e.status;
			var eid=e.id;
		
			if (found=="found") {
				if (eid != undefined) {
						var return_this='<div class="errors" role="alert">\
		#Server '+data+' has no id, <a data-toggle="modal"\
			href="#invitecontainer1"\
			onclick="javascript:addHost('+wrapIt(user)+','+wrapIt(data)+', '+wrapIt(groupId)+');">add\
			Host?</a>\
	</div>';
					$('#return_result').html(return_this);
					
					$('#sshUsername').val("");
			}
			}
			});
		}	
	}

	var user="${backuser}"
	function resetMsg() { 
		$('#return_result').html("");
		$('#response_image').html("");
	}

	function addSelection(email, data) {
		$('#selectedValues').append('<div class="btn"></div><input type="checkbox" id="sList" checked name="serverList" value="'+data+'">'+data+'</div>');
	}
	
	

</g:javascript>