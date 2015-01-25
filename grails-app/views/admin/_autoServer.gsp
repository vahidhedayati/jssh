
<div id="userInputError">
	<div id="userInput">
		<input type='text' id='serverName' name="serverName"
			onClick="this.value='';resetMsg();"
			onchange="verifyValue(this.value);" />
	</div>
	<div id="response_image"></div>
</div>
<div id="return_result"></div>

<g:javascript>

	function verifyValue(data) {
		if (data!='') {
			$.getJSON('${createLink(controller:"connectSsh", action: "findServer")}?hostName='+data,function(e){
			var found=e.status;
			var eid=e.id;
		
			if (found=="found") {
				if (eid != undefined) {
					$('#response_image').html('<div id="accept"></div>');
					$('#return_result').html('Server found: '+eid);
					addSelection(eid, data);
					
					$('#serverName').val(" ");
					resetMsg();
				}else{
					$('#response_image').html('<div id="question"></div>');
					var return_this='<div class="errors" role="alert">\
		#Server '+data+' has no id, <a data-toggle="modal"\
			href="#invitecontainer1"\
			onclick="javascript:addHost('+wrapIt(user)+','+wrapIt(data)+');">add\
			Host?</a>\
	</div>';
					$('#return_result').html(return_this);
				}
			} else{
				$('#response_image').html('<div id="reject"></div>');
				var return_this='<div class="errors" role="alert">\
		Server '+data+' not found, <a data-toggle="modal"\
			href="#invitecontainer1"\
			onclick="javascript:addHost('+wrapIt(user)+','+wrapIt(data)+');">add\
			Host?</a>\
	</div>';
				$('#return_result').html(return_this);
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