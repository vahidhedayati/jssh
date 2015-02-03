
<div class="modal fade" id="masterAdminContainer" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div id="adminContainer"></div>
		</div>
	</div>

</div>

<g:javascript>

	function showServers(value) {
		$('#serverRow').show();
		document.getElementById('gpId').value = value;
	}

	function showBlackList(value) {
		$('#serverRow').show();
		document.getElementById('shId').value = value;
	}
	
	function sendGroup() {
		var group = document.getElementById('groupName').value;
		webSocket${divId}.send("/addGroup ,"+group);
	}
	
	function closeModal() {
		$('#masterAdminContainer').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>