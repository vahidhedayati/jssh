<g:if test="${!hideBroadCastBlock.toString().equals('YES')}">
<jssh:broadcast 
jsshUser="someId"
jobName="exampleMultiConnect" 
divId="logs1"

/>
</g:if>
<g:if test="${servers }">

	<g:each in="${servers }" var="ss">
		<jssh:conn 
		hostname="${ss.hostName}"
		 
		username="${ss.sshuser.username}"
		sshKeyPass = "${ss.sshuser.sshKeyPass }"
		sshKey="${ss.sshuser.sshKey}"
		port="${ss.sshPort}"
		 
		jobName="exampleMultiConnect"
		userCommand="${userCommand}"
		adminTemplate="/jsshadmin/blankAdmin"
			
		jsshUser="${jsshUsername+"_"+ss.id}"
		realUser="${jsshUsername }"
			 
			 
		hideWhatsRunning="${hideWhatsRunning}"
		hideDiscoButton="${hideDiscoButton}"
		hidePauseControl="${hidePauseControl}"
		hideSessionCtrl="${hideSessionCtrl}"
		hideConsoleMenu="${hideConsoleMenu}"
		hideSendBlock="${hideSendBlock}"
		hideBroadCastBlock="${hideBroadCastBlock}"
			
			
		divId="divId${ss.id}" 
		/>
		
		
	</g:each>

</g:if>
<g:else>
No servers have been defined!
</g:else>