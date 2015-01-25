<g:each in="${serverList }" var="sl">
<div class="btn"></div><input type="checkbox" id="sList" checked name="serverList" value="${sl.hostName}">${sl.hostName}</div>
</g:each>
