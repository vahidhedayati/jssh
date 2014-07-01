<!DOCTYPE html>
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'jssh.input.label', default: 'SSH Input')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		
<style type="text/css" media="screen">
#console {
  background: #000;
  border: 3px groove #ccc;
  color: #FFF; 
  width: 98%;
  height: 110px;
  white-space: pre;
  text-align: left;
  font-family: monospace;
  display: inline-block;
  margin: 6px 6px 6px 6px;
  padding: 12px 12px 12px 12px;
  border-style: solid;
  border-width: thin;
  border-color: black;
  font-size: 1em;
}
			
</style>

<g:form method="post" action="process">
<g:textField name="username" placeholder="Username?"/>
<g:textField name="password" placeholder="password?"/>
<g:textField name="hostname" placeholder="hostname?"/>
<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
<g:actionSubmit value="process"/>
</g:form>