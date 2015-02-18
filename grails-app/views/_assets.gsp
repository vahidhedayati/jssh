
<asset:stylesheet href="jssh.css" />
<asset:javascript src="jssh.js" />
<g:if test="${loadJQuery.toString().equals('true') }"> 
	<asset:javascript src="jquery.min.js" />
</g:if>
<g:if test="${loadBootStrap.toString().equals('true') }">
	<asset:stylesheet href="bootstrap.min.css" />
	<asset:javascript src="bootstrap.min.js" />
</g:if>
