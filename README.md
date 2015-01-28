jssh
====

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":jssh:1.0" 

This plugin provides a variety of taglib calls within your application to then interact with SSH connection to a Unix/Linux machine. Once you have successfully configured connected your browser will provide something similar to a shell console and with the later Websocket methods you can literally interact live with your SSH connection to the back end Linux host.

##### Releasing 0.29-SNAPSHOT to fix a variety of bugs in 0.29 and set the ground for the future of the plugin.
I am releasing this because it is the ground works for the next phase which requires changes within the code. There were quite a lot of issues with 0.29 and this will fix a lot of those bugs + enhancements in the new feature although not complete. Still useful.


## Config.groovy additions required: 

[Config.groovy variables](https://github.com/vahidhedayati/jssh/wiki/Config.groovy-values)

## Change Release information
 
[Change information](https://github.com/vahidhedayati/jssh/wiki/VersionInfo)

## Video

Video of jssh 0.9, whilst waiting on creations of stuff there was some discussion into the back-end plugin code and how it interacts via websockets:

[jssh 1.0 broadcast ssh commands to multiple remote hosts 8 Mins](https://www.youtube.com/watch?v=HcJauTC6b8I)

[jssh 0.9 full walk through 43 mins? wow a lot of BS :) ](https://www.youtube.com/watch?v=r-dBVUmT9Uo)

Will be releasing a new video to cover multi broadcast method once I get it working hopefully be releasing jssh-1.0 then.
	 	


## Interaction Methods:

##### [Socket Client/Server](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/scsocketconnect.gsp)
#### [mutli-connection broadcasting to multiple SSH connections](https://github.com/vahidhedayati/jssh/wiki/mutli-connection---broadcasting-to-multi-nodes)
[New Client/Server Websocket SSH tag lib call](https://github.com/vahidhedayati/jssh/wiki/Websocket-client-server-taglib-call)

This is the my new segment of the project, in the end I am looking to acheive a web based solution to easily interact with multiple ssh connections and be able to broadcast one command to a group of servers. At the moment on this 029-SNAPSHOT version the ground work is in place. The logic at the end to connect to many is wrong so I need to write some new code/solution for it. 

This new method client/server websockets is a cool feature that makes more of a secure connection. The model is upon triggering the call you actually make 2 websocket connections. Your webpage is 1 and is in essence a dumb terminal that sits awaiting information. It can also send remote commands that again as a dumb terminal the messages are sent through websockets to its parent or master connection which resides on the actual webserver. The webserver makes the connection the websocket client sitting locally and sends the SSH information to it. The client on Server then forwards all of the output back to the dumb terminal being your browser. 

jsshUser = This could be your actual logged in user, since it will now actually log their interactions with SSH, what they connect to and what commands they send. There is no UI available for this at the moment. If not provided a random jsshUser is generated.

On the main index screen there is now a new connection method, if you do use it. You will find once connected an admin cog will appear. You can register initially an SSH group, add to this group some servers and then add SSH servers. For now it does not actually work beyond 1 server :). This requires a major rethink and should only be code at my end i.e. not changes to your DB etc.... The purpose is multi connection global command sends.. local command sends etc. 



```gsp

<jssh:conn hostname="${hostname}" username="${username}"
	port="${port}" password="${password}"
	userCommand="${userCommand}"
	jsshUser="${jsshUser}" divId="${divId}" />
<div id="${divId}"></div>
```					

		
##### [Socket Method](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/socketprocess.gsp)

[jssh-websocket-taglib-call](https://github.com/vahidhedayati/jssh/wiki/jssh-websocket-taglib-call)

[wiki on jssh existing app using websockets](https://github.com/vahidhedayati/jssh/wiki/jssh-websocket-within-existing-application)

[Socket taglib with pingpong](https://github.com/vahidhedayati/jssh/wiki/socket-taglib-with-pingpong)


```gsp
<jssh:socketconnect 
username="${username }"
password="${password }"
hostname="${hostname}" 
userCommand="${userCommand}"
divId="${divId}"
hideWhatsRunning="${hideWhatsRunning }"
hideDiscoButton="${hideDiscoButton }"
hidePauseControl="${hidePauseControl }"
hideSessionCtrl="${hideSessionCtrl }"
hideConsoleMenu="${hideConsoleMenu}"
hideSendBlock="${hideSendBlock}"
hideNewShellButton="${hideNewShellButton}"

/>
```

##### Socket Method -- Multipe calls - remoteForm - refer to plugin connectSsh/index page and go to remote Form..

As above but:

```gsp
<jssh:socketconnect 
...
  divId="${divId}" />
<div id="${divId}"></div>
```

##### [Ajax Taglib](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/ajaxprocess.gsp)

[wiki on jssh existing app using ajax polling](https://github.com/vahidhedayati/jssh/wiki/using-jssh-within-existing-application)

[ajax/polling tag lib call](https://github.com/vahidhedayati/jssh/wiki/ajax-polling-taglib-call)

```gsp
<jssh:ajaxconnect hostname="${hostname}" username="${username}"
	port="${port}" password="${password}"
	userCommand="${userCommand.encodeAsJavaScript()}"
	jsshUser="${jsshUser}"  />
```


##### Misc Calling methods: 

[Taglib example on resources based grails app](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-resources.gsp)

[Taglib example on assets based grails app](https://github.com/vahidhedayati/jssh-test/blob/master/grails-app/views/testjssh/using-assets.gsp)

[gsp call](https://github.com/vahidhedayati/jssh/wiki/call-directly-via-gsp)

[Extendable css div box](https://github.com/vahidhedayati/jssh/wiki/extending-SSH-Connection-boxes)

[Easy scrolling tailing log test](https://github.com/vahidhedayati/jssh/wiki/tail-dummy-log-file)

[Usercommand hacks](https://github.com/vahidhedayati/jssh/wiki/userCommand-hacks)

[Bootstrap/jquery switch method](https://github.com/vahidhedayati/jssh/wiki/Bootstrap---Jquery-Switch-method)

[Screenshots](https://github.com/vahidhedayati/jssh/wiki/Screenshots)

SSH Keys:

Bsaically for the new websocket multi method you will need to use keys. And this is the easiest way of setting up the trust. You need to keep the actual keys per account on an accessible file system to the webserver serving this plugin. Since although an end user can configure keys via the web interface / Config.groovy. The actual key files will need read access from tomcat to be able to make SSH connections.

This is really a solution for those lets say that have generic system accounts in place. So admin that can do pretty much everything. Webuser that can do things within the scope of apache and so on. With that in place and the keys available to your site you can then let end users log in with their real ids. Use the shared ids which is all then mapped within the plugin.

This command once you have keys would need to be done per host that needs key access, most people would use puppet or similar to push out the generic keys to all hosts.

```
ssh-copy-id -i $HOME/.ssh/id_rsa.pub $user@$remote_home
```



