jssh
====

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":jssh:1.3" 

This plugin is a web based solution that acts like putty or any other ssh console that requires installation (not easily available on phones etc). It provides a variety of taglib calls that you can call from within your application to then interact with SSH connection(s) to Unix/Linux/Osx machines. 

Once you have successfully configured connected your browser will provide something similar to a shell console and with the later Websocket methods you can literally interact live with your SSH connection(s).




## Config.groovy additions required: 

[Config.groovy variables](https://github.com/vahidhedayati/jssh/wiki/Config.groovy-values)

## Change Release information
 
[Change information](https://github.com/vahidhedayati/jssh/wiki/VersionInfo)

## Videos

Video of jssh 0.9, whilst waiting on creations of stuff there was some discussion into the back-end plugin code and how it interacts via websockets:

[jssh 1.3 sshuser configuration per webuser + command restrictions(blacklist) / command rewrites](https://www.youtube.com/watch?v=tkGavxbrnh8) 

[jssh 1.0 broadcast ssh commands to multiple remote hosts 8 Mins](https://www.youtube.com/watch?v=HcJauTC6b8I)

[jssh 0.9 full walk through 43 mins? wow a lot of BS :) ](https://www.youtube.com/watch?v=r-dBVUmT9Uo)

	 	


## Interaction Methods:

## 1> [Socket Client/Server](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/scsocketconnect.gsp)

##### [mutli-connection broadcasting to multiple SSH connections](https://github.com/vahidhedayati/jssh/wiki/mutli-connection---broadcasting-to-multi-nodes)

[New Client/Server Websocket SSH tag lib call](https://github.com/vahidhedayati/jssh/wiki/Websocket-client-server-taglib-call)

[1.3 Command blacklist / command rewrites](https://github.com/vahidhedayati/jssh/wiki/Websocket-client-server-command-utils)


### [Sessions](https://github.com/vahidhedayati/jssh/wiki/conn-sessions)



```gsp

<jssh:conn 
    hostname="${hostname}" 
    username="${username}"
	port="${port}" 
	password="${password}"
	userCommand="${userCommand}"
	realuser="${session.username}"
	jsshUser="${jsshUser}"  //do not set this if you want auto gen id  
	divId="${divId}"
	enablePong="true"
	pingRate="50000"
 />

```					

		
## 2A> [Socket Method](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/socketprocess.gsp)

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
enablePong="true"
pingRate="50000"
/>
```

## 2B> Socket Method -- Multipe calls - remoteForm - 

As above but: refer to plugin connectSsh/index page and go to remote Form..
I have used this method and called it many times on 1 page by reusing the <jssh:socketconnect tag multiple times.

```gsp
<jssh:socketconnect 
...
  divId="${divId}" />
<div id="${divId}"></div>
```

## 3> [Ajax Taglib](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/ajaxprocess.gsp)

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

#### SSH Keys:

Bsaically for the new websocket multi method you will need to use keys. You need to keep the actual keys per account on an accessible file system to the webserver serving your app, key files require read access for your tomcat user in order to make SSH connections.

This is really a solution for those lets say that have generic system accounts in place. So admin that can do pretty much everything. Webuser that can do things within the scope of apache and so on. With that in place and the keys available to your site you can then let end users log in with their real ids. Use the shared ids which is all then mapped within the plugin. Each webuser can be configured to use lets say admin account in a different way with different blacklists/rewrites.

This command once you have keys would need to be done per host that needs key access, most people would use puppet or similar to push out the generic keys to all hosts.

```
ssh-copy-id -i $HOME/.ssh/id_rsa.pub $user@$remote_home
```

#### Why pingpong ?

Websockets by default can be configured to have a timeout for connections, setting this to 0 will set websockets to not time out.  This principal works usually in a standard websocket implementation. 

(the following is my own clonclusion)

In this plugin SSH connections/commands are sent via async, this puts the websocket request as a background task that pushes messages to frontend as of and when they come in. I think this layer of async breaks the rule of a typical connection whcih is bound and awaiting response/request. 

The reason pingpong was introduced was to keep the async conection alive by sending an invisible ping/pong between backend/frontend connections.
