jssh
=========

Grails jssh Plugin based on j2ssh library : Provides ( RemoteSSH + exec command ) 


Dependency :

	compile ":jssh:0.1" 

This plugin provides  basic functionality to allow you to call your end host either via a taglib or via a call to provided controller. These are just examples and you could either use out of the package or create your own from given examples.


	 	
# Config.groovy variables required:

Configure SSH and SCP by adding properties to grails-app/conf/Config.groovy under the "remotessh" key:


    # 	Option set a global username to access ssh through to remote host
    # 	If you are going to define user from above commands then leave it with empty 
    	speech marks
    jssh.USER = "USER"

    # 	The password leave blank if you are about to use SSH Keys, otherwise provide 
    	password to ssh auth
    jssh.PASS=""

    # 	The ssh key is your id_rsa or id_dsa - please note your tomcat will need 
    	access/permissions to file/location
    jssh.KEY="/home/youruser/.ssh/id_rsa"

    # 	If you use a key pass for your key connections then provide it below
    jssh.KEYPASS=""

    # 	The ssh port to connect through if not given will default to 22
    jssh.PORT="22"
	
	
# Taglib call:
Refer to ConnectSshTagLib.groovy within plugin
```gsp
<jssh:connect hostname="localhost" username="myuser" password="mypass" userComand="sudo tail -f /var/log/syslog"/>
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
### Hitting default controller
http://localhost:8080/YOUR_APP/connectSsh/
![index served by](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/1.jpg)
![example script](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/2.jpg)
![dynamic output](https://raw.github.com/vahidhedayati/jssh-test/master/jssh-doc/3.jpg)

This plugin requires and hacks in jquery on the sample _process.gsp template which simply polls controller for any updates sent to ssh connection. You may wish to tweek this slow down/speed up the millisecond configuration.

The close connection button at the top of the process page is specifically for scripts that may for example hang on to connection i.e. running a tail -f on a log file. This button closes ssh connection and disconnects from remote host.

The whole reason for this plugin was because I had been looking for a way of polling ssh connection through my grails app and my existing remote-ssh plugin was not doing it for me. Found the j2ssh libraries and this got put together in a very short period of time.

TODO: - there is a qAndA in ConnectSsh.groovy which I wish to put to use and allow users to dynamically either link scripts or for an easy way to pass expect style input to back end : i.e. send command expect this send this back, which can be plugged in to existing connection process.


 