<g:if test="${blacklist }">
<g:each in="${blacklist }" var="sl">
<div class="btn"></div><input type="checkbox" id="sList" checked name="blacklist" value="${sl.command}">${sl.command}</div>
</g:each>
</g:if>