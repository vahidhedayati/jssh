jssh
====

### Side note - similar / related projects
- [RemoteSSH](https://github.com/vahidhedayati/RemoteSSH)

- [jssh  (this)](https://github.com/vahidhedayati/jssh)

Grails jssh Plugin based on j2ssh library, provides ssh connection with features/facilities to execute remote shell commands. Provides connection via websockets as well as ajax/polling.  

Websocket ssh interaction can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites. Plugin will work with tomcat 7.0.54 + (8 as well) running java 1.7 +


###### Dependency (Grails 2.X):

	compile ":jssh:1.10"
	
[codebase for grails 2.X](https://github.com/vahidhedayati/jssh/tree/grails2)


###### Dependency (Grails 3.X) :
```groovy
	compile "org.grails.plugins:jssh:3.0.2"
```

	 
	 

This plugin is a web based basic putty i.e. sshkey or username/password. It provides a variety of taglib calls that you can call from within your application to then interact with SSH connection(s) to Unix/Linux/OSx machines. 

Once you have successfully configured connected. Your browser will provide something similar to a shell console and with the latter Websocket calls you can literally interact live with your SSH connection(s).


## Config.groovy additions required: 

[Config.groovy variables](https://github.com/vahidhedayati/jssh/wiki/Config.groovy-values)

Test site:

[grails 3 demo site](https://github.com/vahidhedayati/testjssh-grails3) refer to application.groovy for config values

[grails 2 demo site](https://github.com/vahidhedayati/test-jssh)

## Change Release information
 
[Change information](https://github.com/vahidhedayati/jssh/wiki/VersionInfo-grails3)

## Videos

[jssh 1.6 admin interface / cloning of accounts + viewing historical logs/commands](https://www.youtube.com/watch?v=rGIWeLMeC5o)

[jssh 1.3 sshuser configuration per webuser + command restrictions(blacklist) / command rewrites](https://www.youtube.com/watch?v=tkGavxbrnh8) 

[jssh 1.0 broadcast ssh commands to multiple remote hosts 8 Mins](https://www.youtube.com/watch?v=HcJauTC6b8I)

Video of jssh 0.9, whilst waiting on creations of stuff there was some discussion into the back-end plugin code and how it interacts via websockets:

[jssh 0.9 full walk through 43 mins? wow a lot of BS :) ](https://www.youtube.com/watch?v=r-dBVUmT9Uo)

	 	


## Interaction Methods:

## 1> [Socket Client/Server](https://github.com/vahidhedayati/jssh/blob/master/grails-app/views/connectSsh/scsocketconnect.gsp)

##### [mutli-connection broadcasting to multiple SSH connections](https://github.com/vahidhedayati/jssh/wiki/mutli-connection---broadcasting-to-multi-nodes)

[New Client/Server Websocket SSH tag lib call](https://github.com/vahidhedayati/jssh/wiki/Websocket-client-server-taglib-call)

[1.3 Command blacklist / command rewrites](https://github.com/vahidhedayati/jssh/wiki/Websocket-client-server-command-utils)

1.11 j2ssh some updates to the inner workings, issues getting it to work on latest ubuntu, are you having issues getting j2ssh to work with your open-ssh server ? if so read this

[J2ssh issues with latest open-ssh-server](http://stackoverflow.com/questions/26424621/algorithm-negotiation-fail-ssh-in-jenkins)

J2ssh was not working on the latest ubuntu for me, refer to the above link to find changes in debian ssh roll out.

### [To fix j2ssh and latest openssh-server issue read this](https://github.com/vahidhedayati/jssh/wiki/j2ssh-and-openssh-server-issues)
		 

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



## 4> Admin interface (part of 1.4 release)

A new configuration item has been added to jsshUser DB table, called permissions.

If you want to use this plugin and define admin outside of the scope of usual app interaction, then you could try adding default admin accounts through your :

[BootStrap.groovy](https://github.com/vahidhedayati/jssh/wiki/BootStrap.groovy---adding-admin-accounts)


Otherwise you could add the following to your Config.groovy and any accounts generated from there on via the tool would have access to admin interface.
You could do this to start with then change the defaultperm="user" 
 
```groovy
jssh.defaultperm="admin"
```


In order to access admin interface you need to call this taglib :

```gsp
<jssh:loadAdmin jsshUser="your_userId" />
```

If you have added the admin account via Config.groovy then the account is not generated as yet so you need to make an initial connection to create the user:

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



## 5> Specific user connection select boxes.
This is the component within admin menu that allows the end user to connect to a group of servers. It has been recreated as a taglib call so now you as the end user can define when/where your website users can choose to connect to a group of servers. 

Most basic call with full access to all its features: 
```
<jssh:connectUser 
jsshUser="${session.username}"  
/>

```

A more defined specific call with global broadcast and send blocks pers sever blocked, this also defines primary command to be run upon group selection: 
```
<jssh:connectUser 
jsshUser="${session.username}"  
userCommand="something"
hideSendBlock="YES" 
hideBroadCastBlock="YES"
/>
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
