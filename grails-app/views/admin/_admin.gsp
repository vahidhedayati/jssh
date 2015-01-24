<div class="modal fade" id="masterAdminContainer" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div id="adminContainer"></div>
		</div>
	</div>

</div>

<g:javascript>

	function sendGroup() {
		var group = document.getElementById('groupName').value;
		webSocket${divId}.send("/addGroup ,"+group);
	}
	
	function sendServers() {
		var group = document.getElementById('groupId').value;
		var server = document.getElementById('servername').value;
		webSocket${divId}.send("/addServer "+group+","+server);
	}
	
	function closeModal() {
		$('#masterAdminContainer').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>