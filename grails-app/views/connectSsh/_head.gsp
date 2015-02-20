<head>
<g:if test="${!request.xhr }">
	<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
		<meta name='layout' content="ajssh" />
	</g:if>
	<g:else>
		<meta name='layout' content="jssh" />
	</g:else>
</g:if>
<g:else>
	<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
		<g:render template="/assets"   model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}"/>
	</g:if>
	<g:else>
		<g:render template="/resources"  model="${[loadBootStrap:loadBootStrap, loadJQuery:loadJQuery, loadStyle:loadStyle]}" />
	</g:else>
</g:else>
<title>Grails Jssh plugin</title>
</head>