<head>
<g:if test="${!request.xhr }">
		<meta name='layout' content="ajssh" />
</g:if>
<g:else>
	<g:render template="/assets"   model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}"/>
</g:else>
<title>Grails Jssh plugin</title>
</head>