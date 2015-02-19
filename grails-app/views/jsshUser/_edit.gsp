	<g:render template="/connectSsh/jsshAdmin" />
	
		<div id="edit-applications" class="content scaffold-edit" role="main">
		<g:set var="entityName" value="${message(code: 'jssh.jsshUser', default: 'jsshUser')}" />
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${uiterator}">
			<ul class="errors" role="alert">
				<g:eachError bean="${uiterator}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form controller="connectSsh" method="post" >
				<g:hiddenField name="id" value="${uiterator?.id}" />
				<g:hiddenField name="table" value="${table}" />
				<g:hiddenField name="version" value="${uiterator?.version}" />
				<fieldset class="form">
					<g:render template="/jsshUser/form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>