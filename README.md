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
