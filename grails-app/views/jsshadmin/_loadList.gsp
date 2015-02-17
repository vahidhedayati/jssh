<g:if test="${currentList}">
<g:each in="${currentList}" var="sl">
<div class="btn"></div><input type="checkbox" id="sList" checked name="${listType}" value="${sl.command}">${sl.command}</div>
</g:each>
</g:if>