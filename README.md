jssh
====

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


Dependency :

	compile ":jssh:1.3-SNAPSHOT" 

This plugin is a web based basic putty i.e. sshkey or username/password. It provides a variety of taglib calls that you can call from within your application to then interact with SSH connection(s) to Unix/Linux/OSx machines. 

Once you have successfully configured connected. Your browser will provide something similar to a shell console and with the latter Websocket calls you can literally interact live with your SSH connection(s).




## Config.groovy additions required: 

[Config.groovy variables](https://github.com/vahidhedayati/jssh/wiki/Config.groovy-values)

## Change Release information
 
[Change information](https://github.com/vahidhedayati/jssh/wiki/VersionInfo)

## Videos

[jssh 1.3 sshuser configuration per webuser + command restrictions(blacklist) / command rewrites](https://www.youtube.com/watch?v=tkGavxbrnh8) 

[jssh 1.0 broadcast ssh commands to multiple remote hosts 8 Mins](https://www.youtube.com/watch?v=HcJauTC6b8I)

Video of jssh 0.9, whilst waiting on creations of stuff there was some discussion into the back-end plugin code and how it interacts via websockets:

[jssh 0.9 full walk through 43 mins? wow a lot of BS :) ](https://www.youtube.com/watch?v=r-dBVUmT9Uo)

	 	


## Interaction Methods:

## 0> Admin interface - currently working on it

```gsp
<jssh:loadAdmin jsshUser="your_userId" />
```
Remember to add the following to your config.groovy - so that when a user logs in. Their profile is created as an admin account.
 
```groovy
jssh.defaultperm="admin"
```

In order to create or use the above admin tag lib you must first make a connection so I have a startpage

```gsp

<jssh:conn 
jsshUser="your_userId"
realUser="your_userId"
jobName="vahidsJob"
username="your_userId"
password=""
hostname="HOSTNAME" 
userCommand="tail -f /var/log/tomcat/catalina.out"
divId="abaa"
enablePing="true"
pingRate="60000"
/>
```

Once you have hit the initial start page and created that jssh account to match the same jsshUser as the admin account and have defined admin permission for the user, the admin interface will then come alive.

At the moment you can edit stuff. I want to add cloning features and deletions.



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

#### [SSH Keys:](https://github.com/vahidhedayati/jssh/wiki/ssh-keys)

#### [Why pingpong ?](https://github.com/vahidhedayati/jssh/wiki/why-pingong)
