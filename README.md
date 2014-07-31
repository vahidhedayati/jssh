jssh 0.7
=========

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.


Dependency :

	compile ":jssh:0.7" 

This plugin provides  basic functionality to allow you to call your end host either via a taglib or via a call to provided controller. These are just examples and you could either use out of the package or create your own from given examples.


# 0.5 release : websocket live ssh interaction:
Using default tomcat websockets to interact with j2ssh libraries. It is really very powerful/amazing stuff.
![websocket connection](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/4.jpg)

I have found whilst tailing a file if I execute a command it actually executes the new command then output resumes back to original tailing  (please refer to configuration items below, this has to be enabled as part of 0.6+). 

No ctrl C or special keys etc. If you are connected and wish to stop such features simply click the big disconnect button or close browser.
![send remote command even whilst tailing !](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/5.jpg)


Javascript detection of browser changes i.e. leaving page/closing browser and will notify websockets to attempt to close ssh connection down.

On the bright side, there are pause and resume buttons provided on websockets which pauses output and on resume pumps back StringBuffer that was preserving the output whilst you had it on pause.

New taglibs added (check under tag lib)



	 	
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
* This is the most important configuration required for websocket calls
* in my current version the hostname is being defined by tomcat start up setenv.sh
* In my tomcat setenv.sh I have
* HOSTNAME=$(hostname)
* JAVA_OPTS="$JAVA_OPTS -DSERVERURL=$HOSTNAME"
*
* Now as per below the hostname is getting set to this value
* if not defined wschat will default it localhost:8080
*
*/
jssh.wshostname=System.getProperty('SERVERURL')+":8080"


/*
* This is yet another important functionality for websocket connections
* it stands for NEW CONNnection PER TRANSaction
* This is as described below default feature of 0.5
* from 0.6 it will attempt to reuse existing connection 
*
* Pros of this being not set  is if you execute sudo -i
* then execute a new command you are still continuing previous call
* If you enable this function - sudo -i will be one connection and the next command will be a new shell
* Cons of this not being enabled - if you are in a tail -f your new command will not execute
*
* Pros/cons of enabling this function is reverse of all of above
*/ 
jssh.NEWCONNPERTRANS='YES'

```	


# Using plugin within existing application
| [wiki on jssh existing app using websockets](https://github.com/vahidhedayati/jssh/wiki/jssh-websocket-within-existing-application) | 
[wiki on jssh existing app using ajax polling](https://github.com/vahidhedayati/jssh/wiki/using-jssh-within-existing-application) |


	
# Taglib call:
Refer to ConnectSshTagLib.groovy within plugin

Define a template= if you wish to override default _process.gsp template from loading

```gsp
<!-- ------------------------------------------------------------ -->

<!-- New websockets connector tag lib -->
<jssh:socketconnect hostname="localhost" username="myuser" 
template="/optional/file"
port="{optional : your ssh port}"
password="mypass" 
userCommand="sudo tail -f /var/log/syslog"
divId="logs2"

/>




<!-- ------------------------------------------------------------ -->
<!-- To use old method (ajax/polling) -->
<jssh:connect hostname="localhost" username="myuser" 
template="/optional/file"
port="{optional : your ssh port}"

password="mypass" userCommand="sudo tail -f /var/log/syslog"
/>
<!-- ------------------------------------------------------------ -->


```

 
Remember if you have defined usernames,passwords,keys etc in your config.groovy then there is no need to define those values in any of the calls above..


### divId="logs2"
To call websocket connections multiple times on page, you need to set divId within taglib call as above.. this then means you could tail logs from multiple servers using the socketconnect taglib call.

![multiple websocket taglib calls on default grails site](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/6.jpg)

#### Taglib example on resources based grails app
[resources based calls](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-resources.gsp)

#### Taglib example on assets based grails app
[assets based calls](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-assets.gsp)

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
 0.7 :	Minor change - updated BuildConfig websockets-api set export to false - conflicting with tomcat in production
 
 0.6 : 	UI Updates + fixed channel re-use issues for dynamic calling backend ssh connection 
 		Fixes for remoteForm calls - still not working, left alone for now.
 		Returns missing some calls to the r:layoutResources tag (due to complexity to cater for assets/resources)
 		On a normal site with assets/resources defined - should be fairly easy to fix (local calls).
 		
 		
 0.5 : 	Websockets added to j2ssh - and we are rocking !
 		Websockets remoteForm method added, which can be used as an example 
 		for having multiple calls on the same page using front end forms. 
 		Check out taglibs for direct calls from within gsps.
  		Old ajax calls left alone.
  		
 0.4 :	content passed from j2ssh to view is now changed to return appended value 
 		rather than entire replacement
 		of content. As a result a few things changed, buffer sizing removed from 
 		_process.gsp - 
 		content is now ongoing rolling content from back end appended to HTML view.
 		SSH port configuration enabled
 		
 0.3 : 	Unlimited BufferSize added by setting value as 0, _process template override 
 		feature provided.
 		by either posting a form which has new template value or in TAGlib adding 
 		template="/path/to/my/template"
 		BufferSize connection input type added to process page on mouseOut will set 
 		the value as what user defines 
 		on process page
 
 0.2 :	added auto scrolling to terminal window, fixed close connection to work 
 		regardless of connection or not. Added friendly messages for bad login, 
 		host connection issues. Added bootstrap CSS + buttons.
 		BUFFFERSIZE added as a configuration option to ease on browser slowing down.
```
