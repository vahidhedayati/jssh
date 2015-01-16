jssh
====

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":jssh:0.28-SNAPSHOT1" 

This plugin provides  basic functionality to allow you to call your end host either via a taglib or via a call to provided controller. These are just examples and you could either use out of the package or create your own from given examples.


### Video:
Video of jssh 0.9, whilst waiting on creations of stuff there was some discussion into the back-end plugin code and how it interacts via websockets: [jssh 0.9 full walk through](https://www.youtube.com/watch?v=r-dBVUmT9Uo)


### 0.5 release : websocket live ssh interaction:
Using default tomcat websockets to interact with j2ssh libraries.
![websocket connection](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/4.jpg)

I have found whilst tailing a file, if required you could click New Shell which will duplicate your connection on your existing screen and allow you to type further commands.  if executed, the new command will be sent and its output appended to your connection. After which it will resumes back to original tailing  (please refer to configuration items below, this has to be enabled as part of 0.6+). 

No ctrl C or special keys etc. If you are connected and wish to stop such features simply disconnect or close your browser.

![send remote command even whilst tailing !](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/5.jpg)


Javascript detection of browser changes i.e. leaving page/closing browser and will notify websockets to attempt to close ssh connection down.

On the bright side, there are pause and resume buttons provided on websockets which pauses output and on resume pumps back StringBuffer that was preserving the output whilst you had it on pause.

New taglibs added (check under tag lib)


	 	
## [Config.groovy variables](https://github.com/vahidhedayati/jssh/wiki/Config.groovy-values)



#### Using plugin within existing application
| [wiki on jssh existing app using websockets](https://github.com/vahidhedayati/jssh/wiki/jssh-websocket-within-existing-application) | 
[wiki on jssh existing app using ajax polling](https://github.com/vahidhedayati/jssh/wiki/using-jssh-within-existing-application) |


	
### Taglib call:

Refer to ConnectSshTagLib.groovy within plugin

Define a template= if you wish to override default _process.gsp template from loading

#### [Websockets tag lib call](https://github.com/vahidhedayati/jssh/wiki/websocket-taglib-call)

##### [Socket taglib with pingpong](https://github.com/vahidhedayati/jssh/wiki/socket-taglib-with-pingpong)

##### [ajax/polling tag lib call](https://github.com/vahidhedayati/jssh/wiki/ajax-polling-taglib-call)

![multiple websocket taglib calls on default grails site](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/6.jpg)

##### [Taglib example on resources based grails app](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-resources.gsp)

##### [Taglib example on assets based grails app](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-assets.gsp)

##### [gsp call](https://github.com/vahidhedayati/jssh/wiki/call-directly-via-gsp)

##### [Extendable css div box](https://github.com/vahidhedayati/jssh/wiki/extending-SSH-Connection-boxes)

##### [Easy scrolling tailing log test](https://github.com/vahidhedayati/jssh/wiki/tail-dummy-log-file)

##### [Usercommand hacks](https://github.com/vahidhedayati/jssh/wiki/userCommand-hacks)

##### [Bootstrap/jquery switch method](https://github.com/vahidhedayati/jssh/wiki/Bootstrap---Jquery-Switch-method)

### [Change information](https://github.com/vahidhedayati/jssh/wiki/VersionInfo)
