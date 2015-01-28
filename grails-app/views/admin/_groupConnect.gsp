<jssh:broadcast 
jsshUser="someId"
jobName="exampleMultiConnect" 
divId="logs1"
/>

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
			adminTemplate=""
			jsshUser="${jsshUsername+"_"+ss.id}" 
			divId="divId${ss.id}" 
			/>
		<div id="divId${ss.id}"></div>
	</g:each>

</g:if>
<g:else>
No servers have been defined!
</g:else>