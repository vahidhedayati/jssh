<g:if test="${servers && user }">

	<g:each in="${servers }" var="ss">
		
		<jssh:conn hostname="${ss.hostName}" username="${user.username}"
			port="${ss.sshPort}" 
			jobName="${jobName}"
			userCommand="${userCommand}"
			jsshUser="${jsshUsername+"_"+ss.hostName}" divId="${ss.hostName}" />
		<div id="${ss.hostName}"></div>
	</g:each>

</g:if>