<ul  class="nav navbar-nav">
<li>
	<a class="navbar-brand" href="${createLink(uri: '/')}" id="size3">
		<g:if test="${session.jsshuser}">
			${session.jsshuser } 
			<g:if test="${session.isAdmin.toString().equals('true')}"> | Administrator</g:if>
 			| 
 		</g:if>
		${meta(name:'app.name')}
	</a>
</li>
</ul>
		