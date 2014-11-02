jssh 0.22
=========

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":jssh:0.22" 

This plugin provides  basic functionality to allow you to call your end host either via a taglib or via a call to provided controller. These are just examples and you could either use out of the package or create your own from given examples.


# Video:
I have made a video of a site from scratch with jssh 0.9, whilst waiting on creations of stuff there was some discussion into the backend plugin code and how it interacts via websockets: [jssh 0.9 full walk through](https://www.youtube.com/watch?v=r-dBVUmT9Uo)

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
* jssh EndPoint timeout
* by default if not defined this is set to 0
* 0 = do not disconnect on idle activity
* value can be set in milliseconds
*/
jssh.timeout="800200"


/* disable.login
* Typically you should have some form of authentication mechanism 
* from which you would lock down the default index pages that allows interaction
* If you do not set this to YES 
* This will then disable the default index pages that allows interaction. 
*/
jssh.disable.login="NO"


/*--------------------------------------------------------------------------*/
/* WEB PAGE SPECIFIC CONFIGURATION */

/*
* NEWCONNPERTRANS
* Stands for NEW CONNnection PER TRANSaction
* watch the video to understand how / what this is doing..
*/ 
jssh.NEWCONNPERTRANS='NO'


/* hideSessionCtrl
* this controls two buttons giving end user feature to enable new sessions per transaction or not
* set this to YES if you wish the users to see the buttons + control action via front end.
* This must be set to YES if you wish to not let end user override above defined value
*/
jssh.hideSessionCtrl="NO"


/* hideAuthBlock 
* if set to YES will Hide Authentication/server block
* on the main page that loads up form to submit to remote host
* If you have configured 
*/
jssh.hideAuthBlock='NO'


/* hideConsoleMenu
* Has got a little more complex from 0.10.
* If disabled it will attempt to hide all controls such as pause/resume/disconnect
* If disabled it will also check for :  
* hidePauseControl + hideDiscoButton
* if those two are set to no then it will show those two buttons
*/
jssh.hideConsoleMenu="NO"


/* hideSendBlock
* this will come in handy if you wish to hide away remote execution block from end user
* set this to YES if you no longer wish end users to be able to execute remote commands
*/
jssh.hideSendBlock="NO"


/* hidePauseControl
* This hides the control buttons to pause/resume logs
*/
jssh.hidePauseControl="NO"


/* hideWhatsRunning
* This hides the label that shows what host the user is connected to and what command is being executed
*/
jssh.hideWhatsRunning="NO"


/* hideDiscoButton
* This hides the disconnect button
* unsure why you would want to - but maybe you do
*/
jssh.hideDiscoButton="NO"



/* hideNewShellButton
* This hides two buttons new shell / close shell
* as of 0.12  new shell was introduced
* its off of NSPT from earlier versions
* a cleaner way of controlling new shells/closing shells. 
*/

hideNewShellButton="NO"

```	


So besides the configuration items such as username/keys etc for the actual view, I have enabled the following options:

```groovy
jssh.disable.login="YES"
jssh.NEWCONNPERTRANS='NO'
jssh.hideSessionCtrl="YES"
jssh.hideAuthBlock='NO'
jssh.hideConsoleMenu="YES"
jssh.hideSendBlock="YES"
jssh.hidePauseControl="NO"
jssh.hideWhatsRunning="NO"
jssh.hideDiscoButton="NO"
hideNewShellButton="YES"
```

The above now means there are no pages that end users can interact with so far as default plugin goes, when the actual site sends something via this plugin, the view (being socketprocess) disables everything besides pause/resume logs and disconnect.

You can tweak the above to meet your criteria...





# Using plugin within existing application
| [wiki on jssh existing app using websockets](https://github.com/vahidhedayati/jssh/wiki/jssh-websocket-within-existing-application) | 
[wiki on jssh existing app using ajax polling](https://github.com/vahidhedayati/jssh/wiki/using-jssh-within-existing-application) |


	
# Taglib call:
Refer to ConnectSshTagLib.groovy within plugin

Define a template= if you wish to override default _process.gsp template from loading

#### Websockets tag lib call
```gsp

<!-- New websockets connector tag lib -->
<jssh:socketconnect hostname="localhost" username="myuser" 
template="/optional/file"
port="{optional : your ssh port}"
password="mypass" 
userCommand="sudo tail -f /var/log/syslog"
divId="logs2"
/>
```

Complete call allowing you to customise all override values per call or if not defined according to your config.groovy values:
```gsp
<jssh:socketconnect hostname="localhost" username="myuser" 
template="/optional/file"
port="{optional : your ssh port}"
password="mypass" 
userCommand="sudo tail -f /var/log/syslog"
divId="logs2"

wshostname="new_WEBSOCKET_HOSTNAME"
hideWhatsRunning="NO"
hideDiscoButton="NO"
hidePauseControl="NO"
/>
```


#### ajax/polling tag lib call
```gsp
<jssh:connect hostname="localhost" username="myuser" 
template="/optional/file"
port="{optional : your ssh port}"
password="mypass" 
userCommand="sudo tail -f /var/log/syslog"
/>
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

 0.22	:	Tidy up - removal of Holders.config, removed _Events.groovy listener added to plugin descriptor.
 
 0.21 	: 	Last attempt a stab in the dark due to actual app ver compatibility issues.
 			Issue now fixed. Another problem with ajax poll method where outputReset generated a 404 in debug - fixed.
 			
 0.20	:	Javascript added to parse through textarea and send line by line to socket connection.
 			It worked so long as textarea contained 1 command, caused socket errors otherwise.
 			Now should support multiple commands seperated by line breaks within textarea.
 			This should also fix taglib issue http://stackoverflow.com/questions/25852017/grails-taglib-retain-new-line-breaks-from-controller
 			
 0.19 	: 	Timeout defined for end point, pluginbuddy updated.
 
 0.18	:	Moved over to 2.4.2. Tidy up of grails app version called in pluginbuddy
 
 0.17	: 	Issues with SendBlock custom calls not working again another security check 
 			need to revisit

 0.16	: 	Problem related to lockdown if hideSesionCtrl was set to no in config the 
 			check conflicted with custom calls + terminal access.
			Will revisit logic later

 0.15	: 	ssh.disconnect removed from initial command  / unexpected behaviour for 
 			terminal view
	
 0.14 	: 	Tests against 2.4 failed - plugin reviewed fixed for apps 2.4+ - remoteForm 
 			call now working under 2.4 with 0.14 but not on grails pre 2.4
 
 0.13	:	missing tags for new newShellButton added
 
 0.12 	:	New Shell / Close shell buttons added - like a console reconnect 
 			Connection count now displayed - showing how many current connections there 
 			are to backend
 		
 0.11 	: 	taglib override config calls missed out in 0.9/0.10 added in 0.11
 
 0.10	:	New override configuration added to control all aspects of frontend calls.

 0.9 	:	More UI tidyup - added session.close to all close methods, just incase 
 			there is still a session ongoing.
 			Override Config added to enable/disable frontend same ssh connection or 
 			new ssh connection per command sent via websockets. Look for 
 			hideSessionCtrl="YES" in above Config.groovy. 
 
 0.8 	: 	New configuration added to disable/enable aspects on view
 
 0.7 	:	Minor change - updated BuildConfig websockets-api set export to false - 
 			conflicting with tomcat in production
 
 0.6 	: 	UI Updates + fixed channel re-use issues for dynamic calling backend ssh 
 			connection 
 			Fixes for remoteForm calls - still not working, left alone for now.
 			Returns missing some calls to the r:layoutResources tag (due to complexity 
 			to cater for assets/resources)
 			On a normal site with assets/resources defined - should be fairly easy to 
 			fix (local calls).
 		
 		
 0.5 	: 	Websockets added to j2ssh - and we are rocking !
 			Websockets remoteForm method added, which can be used as an example 
 			for having multiple calls on the same page using front end forms. 
 			Check out taglibs for direct calls from within gsps.
  			Old ajax calls left alone.
  		
 0.4 	:	content passed from j2ssh to view is now changed to return appended value 
 			rather than entire replacement
 			of content. As a result a few things changed, buffer sizing removed from 
 			_process.gsp - 
 			content is now ongoing rolling content from back end appended to HTML view.
 			SSH port configuration enabled
 		
 0.3 	: 	Unlimited BufferSize added by setting value as 0, _process template 
 			override 
 			feature provided.
 			by either posting a form which has new template value or in TAGlib adding 
 			template="/path/to/my/template"
 			BufferSize connection input type added to process page on mouseOut will set 
 			the value as what user defines 
 			on process page
 
 0.2 	:	added auto scrolling to terminal window, fixed close connection to work 
 			regardless of connection or not. Added friendly messages for bad login, 
 			host connection issues. Added bootstrap CSS + buttons.
 			BUFFFERSIZE added as a configuration option to ease on browser slowing down.
```