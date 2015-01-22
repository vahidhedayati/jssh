<g:javascript>
function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&amp;/g,'&');
}



function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
 }



// Should be loaded from higher level
// added just incase this page is called 
// from outside of the scope of default plugin method
function toggleBlock(caller,called,calltext) {
	$(caller).click(function() {
		if($(called).is(":hidden")) {
 			$(caller).html('HIDE '+calltext).fadeIn('slow');
    	}else{
        	$(caller).html('SHOW '+calltext).fadeIn('slow');	
        	
    	}
 		$(called).slideToggle("fast");
 	
  	});
  }	



    
	function scrollToBottom${divId}() {
    	$('#messagesTextarea${divId}').scrollTop($('#messagesTextarea${divId}')[0].scrollHeight);
    	//$('#preTextarea${divId}').scrollTop($('#preTextarea${divId}')[0].scrollHeight);
	}
	

	function actOnEachLine(textarea, func) {
    	var lines = textarea.value.replace(/\r\n/g, "\n").split("\n");
    	var newLines, newValue, i;

    	// Use the map() method of Array where available 
    	if (typeof lines.map != "undefined") {
 	       newLines = lines.map(func);
    	} else {
        	newLines = [];
        	i = lines.length;
        	while (i--) {
            	newLines[i] = func(lines[i]);
        	}
    	}
    	textarea.value = newLines.join("\r\n");
	}


	$('#textMessage${divId}').keypress(function(e){
  		if (e.keyCode == 13 && !e.shiftKey) {
     		e.preventDefault();
		}
		if(e.which == 13){
       		var tmb=textMessage.value.replace(/^\s*[\r\n]/gm, "");
       		if (tmb!="") {
       			sendMessage${divId}();
        	}
       }
     });
     </g:javascript>