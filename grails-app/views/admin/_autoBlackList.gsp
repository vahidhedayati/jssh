
<div id="userInputError">
	<div id="userInput">
		<input type='text' id='command' name="command" value="${command}" onClick="this.value='';resetMsg();" onchange="verifyCommandValue(this.value);" />
	</div>
	<div id="response_image"></div>
</div>
<div id="return_result"></div>
<input type=hidden name="shId" id="shId">
<g:javascript>
	
	function verifyCommandValue(data) {
		var shId = document.getElementById('shId').value;
		if ((data != '') && (shId != '')) {
		
			$.getJSON('${createLink(controller:"connectSsh", action: "findSshCommand")}?type=blacklist&command='+data,function(e){
			var found=e.status;
			var eid=e.id;
			if (found=="found") {
				if (eid != undefined) {
						var return_this='<div class="errors" role="alert">\
		Command '+data+' has no id, <a data-toggle="modal"\
			href=""\
			onclick="javascript:autoBlackList('+wrapIt(user)+','+wrapIt(data)+', '+wrapIt(sshUserId)+');">add\
			To BlackList?</a>\
	</div>';
					$('#return_result').html(return_this);
					
					$('#command').val("");
			}
			} else{
				$('#response_image').html('<div id="reject"></div>');
				var return_this='<div class="errors" role="alert">\
		Command '+data+' not found, <a data-toggle="modal"\
			href=""\
				onclick="autoBlackList('+wrapIt(user)+','+wrapIt(data)+', '+wrapIt(shId)+');">add\
			To BlackList?</a>\
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
		$('#selectedValues').append('<div class="btn"></div><input type="checkbox" id="serverList" checked name="blackList" value="'+data+'">'+data+'</div>');
	}
	
	

</g:javascript>