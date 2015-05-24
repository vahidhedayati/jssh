<html>
<head>
<g:render template="/assets" />
</head>
<body>
<div class="logconsolebar">
	<div class="btn btn-primary">
		<b> ${hostname }: running command
		</b>
	</div>
	<g:link class="btn btn-danger" controller="connectSsh"
		action="closeConnection">Close connection</g:link>
	<div class="tbutton1">
		<input type="checkbox" name="pauseLog" id="pauseLog"
			onChange="TriggerFilter(this)"> Pause logs
	</div>
</div>


<pre class="logconsole-lg" id="inspect">
	${input ?: 'Please be patient - logging will start shortly' }	
</pre>

<g:javascript>
	var t;
	var r;
	var baseapp="${meta(name:'app.name')}";
 	
	function getApp() {
		return baseapp;
	}
     function escapeHtml(unsafe) {
		    return unsafe
		         .replace(/&/g, "&amp;")
		         .replace(/</g, "&lt;")
		         .replace(/>/g, "&gt;")
		         .replace(/"/g, "&quot;")
		         .replace(/'/g, "&#039;");
     }

    function refPage() {
		var elem = document.getElementById('inspect');
		elem.scrollTop = elem.scrollHeight;
    }
    function pollPage() {
		getOnline();

		t = setTimeout('pollPage()', 5000);
		r = setTimeout('refPage()', 1000);
    }
    function stopPage() {
		clearTimeout(t);
		clearTimeout(r);
    }
    pollPage();
    function TriggerFilter(e) {
		if (e.checked == true) {
			stopPage();
		} else {
			pollPage();
		}
   };
</g:javascript>
</body>
</html>