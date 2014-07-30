jssh 0.4
=========

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands.


Dependency :

	compile ":jssh:0.4" 

This plugin provides  basic functionality to allow you to call your end host either via a taglib or via a call to provided controller. These are just examples and you could either use out of the package or create your own from given examples.


	 	
# Config.groovy variables required:

Configure SSH and SCP by adding properties to grails-app/conf/Config.groovy under the "remotessh" key:

```groovy
/*
* Option set a global username to access ssh through to remote host
* If you are going to define user from above commands then 
* leave it with empty speech marks
*/
jssh.USER = "USER"


/*
* The password leave blank if you are about to use SSH Keys, 
* otherwise provide password to ssh auth
*/
jssh.PASS=""


/* 
* The ssh key is your id_rsa or id_dsa - please note your 
* tomcat will need access/permissions to file/location
*/
jssh.KEY="/home/youruser/.ssh/id_rsa"


/*
* If you use a key pass for your key connections then provide it below
*
/
jssh.KEYPASS=""


/*
*The ssh port to connect through if not given will default to 22
*/
jssh.PORT="22"


/*
* your ssh port - by default if not defined it will attempt port 22
*
*/
jssh.PORT=22

```	


# Using plugin within existing application
[wiki](https://github.com/vahidhedayati/jssh/wiki/using-jssh-within-existing-application)


	
# Taglib call:
Refer to ConnectSshTagLib.groovy within plugin
Define a template= if you wish to override default _process.gsp template from loading
```gsp
<jssh:connect hostname="localhost" username="myuser" 
template="/optional/file"
password="mypass" userComand="sudo tail -f /var/log/syslog"
/>
```

# gsp call:
Refer to jssh-test project on github and look at views/testjssh/index.gsp
```gsp
<g:form method="post" action="process">
<g:textField name="username" placeholder="Username?"/>
<g:textField name="password" placeholder="password?"/>
<g:textField name="hostname" placeholder="hostname?"/>
<textarea name="command" id="console" placeholder="Line separated commands to execute"></textarea>
<g:actionSubmit value="process"/>
</g:form>
``` 	
## Hitting default controller
http://localhost:8080/YOUR_APP/connectSsh/
![index served by](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/1.jpg)
![example script](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/2.jpg)
![dynamic output](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/3.jpg)

This plugin requires jquery which has been manually loaded in _process.gsp template which simply polls controller for any updates sent to ssh connection. You may wish to tweak this slow down/speed up the millisecond configuration.

The close connection button at the top of the process page is specifically for scripts that may for example hang on to connection i.e. running a tail -f on a log file. This button closes ssh connection and disconnects from remote host.

The whole reason for this plugin was because I had been looking for a way of polling ssh connection through my grails app and my existing remote-ssh plugin was not doing it for me. Found the j2ssh libraries and this got put together in a very short period of time.

#### Extendable css div box
You should find the box that displays output is resizable, hold bottom right hand corner and drag up or down to expand retract..


TODO: - there is a qAndA in ConnectSsh.groovy which I wish to put to use and allow users to dynamically either link scripts or for an easy way to pass expect style input to back end : i.e. send command expect this send this back, which can be plugged in to existing connection process.


### Easy scrolling log test:
Bash one liner to log whilst you execute a command to tail -f /tmp/test.log

```bash
for i in {0..1000000}; do sleep 3s; echo "$(date +%d-%M-%y-%H:%m:%S) $i" >> /tmp/test.log; done

```

##### Bootstrap/jquery switch method
If you are using jquery slider or bootstrap switch, using fontsawesome you could do thumbs up/down for stopping logs. Current default method provided by plugin is a checkbox.
  
```gsp
<input type="checkbox" name="pauseLog"  id="pauseLog" data-size="small" data-label-text="Pause" data-on-text="<span class='fa fa-thumbs-up'></span>" data-off-text="<span class='fa fa-thumbs-down'></span>">
<g:javascript>
	$('#pauseLog').bootstrapSwitch();
	var t;
	var r;
   function getOnline() {
    	 $.get('${createLink(controller:"connectSsh", action: "inspection")}',function(data){
    			$('#inspect').append(data);
    			 resetOutput();
 		});
    }
    function resetOutput() { 
    	$.get('${createLink(controller:"connectSsh", action: "resetOutput")}',function(data){
		});	
    }
   	function refPage() {
   		var elem = document.getElementById('inspect');
  		elem.scrollTop = elem.scrollHeight; 
    }
    function pollPage() {
        getOnline();
         t = setTimeout('pollPage()', 5000);
         r= setTimeout('refPage()', 1000);
    }
    function stopPage() {
        clearTimeout(t);
        clearTimeout(r);
    }
    pollPage();
    $('#pauseLog').on('switchChange', function (e, data) {
		if (new String(data.value).valueOf() == new String("true").valueOf()) {
    		stopPage();
    	}	else{
    		pollPage();
    	}
    });
</g:javascript>
```

# Change information:
```
 0.4 :	content passed from j2ssh to view is now changed to return appended value rather than entire replacement 
 		of content. As a result a few things changed, buffer sizing removed from _process.gsp - 
 		content is now ongoing rolling content from back end appended to HTML view.
 		SSH port configuration enabled
 		
 0.3 : 	Unlimited BufferSize added by setting value as 0, _process template override feature provided.
 		by either posting a form which has new template value or in TAGlib adding template="/path/to/my/template"
 		BufferSize connection input type added to process page on mouseOut will set the value as what user defines 
 		on process page
 
 0.2 :	added auto scrolling to terminal window, fixed close connection to work 
 		regardless of connection or not. Added friendly messages for bad login, 
 		host connection issues. Added bootstrap CSS + buttons.
 		BUFFFERSIZE added as a configuration option to ease on browser slowing down.
```
