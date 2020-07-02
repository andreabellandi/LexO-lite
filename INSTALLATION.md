# LexO-lite Installation Guide

This guide aims to provide compilation and installation instructions for those who are not familiar with *Java* or *Apache Tomcat.*

## Requirements

You need:
-   Apache Tomcat v8.0 or later
-   MySQL v5.0 or later
-   Java 1.8

### Configuring Apache Tomcat

If you haven't already, install Tomcat: `apt install tomcat9`

Check if it's running by visiting `http://localhost:8080` in a webbrowser.

Tomcat needs access to the LexO-lite configuration/lexicon-folder wich is located at
`/.LexO-lite/`, but since Tomcat is sandboxed to certain folders  by *systemd* you need to give it access to the LexO folder.
Create the directory `/etc/systemd/system/tomcat9.service.d/` and in it create the file `override.conf` containing:
```ini
[Service]
ReadWritePaths=/.LexO-lite/
```
Then restart the service by executing:
```bash
systemctl daemon-reload
systemctl restart tomcat9
```

### Creating User Database

LexO-lite requires a UTF-8 Database `LexO_users` to store user data.

 1. Open the MySQL console by typing `mysql`
 2. Create the database LexO_users: 
	```sql 
	CREATE DATABASE LexO_users CHARACTER SET utf8 COLLATE utf8_bin;
	```
3. Create a user and give it access to the database:
	```sql
	CREATE USER 'USERNAME'@'localhost' IDENTIFIED BY 'PASSWORD';
	GRANT ALL ON LexO_users.* TO 'USERNAME'@'localhost';
	FLUSH PRIVILEGES;
	```
	>Replace *USERNAME* and *PASSWORD* with your preferred ones.


## Compilation

For compiling we will use **Maven**. 

```
apt install maven
```

### Compiling Dependencies

To compile LexO-lite you first need to compile the dependencies [OWL-API](https://github.com/owlcs/owlapi) and [SPARQL-DL](https://github.com/protegeproject/sparql-dl-api) .

Do the following for every dependecy:

 1. Download dependency from github
 2. Navigate to the folder that contains the file *pom.xml*
 3. execute the command `mvn install`

### Compiling LexO-lite

 1. Download the LexO-lite source-code from github
 
2. Before compiling you need to tell LexO to use the MySQL database and user that you've created earlier.
To do this navigate to `LexO-lite-master/src/main/resources/` and open the file `hibernate.cfg.xml`.

	Change the lines
	```xml
	<property name="hibernate.connection.username">root</property>
	<property name="hibernate.connection.password">root</property>
	```
	to the username and password you created for your LexO_users database and save the file.

 2. Navigate back to the folder that contains the file *pom.xml*
 3. execute the command `mvn install`
 4. A new folder `target` has been created, in it you will find the file `LexO-lite-1.0-SNAPSHOT.war`
 
 ## Deploying

1. Stop Apache Tomcat:
	```
	systemctl stop tomcat9
	```

1. Create the config/lexicon-folder `/.LexO-lite/` .
1. In it create the file `lexolite.properties` containing:
	```ini
	lexiconFileName=mylexicon.owl
	lexiconNamespace=http\://lexica/mylexicon\#
	lexiconFolder=.LexO-lite/
	lexiconExportFileName=exportedLexicon
	```
1. (OPTIONAL) If you want to include an ontology, also add the following to `lexolite.properties`:
	```ini
	domainOntologyFileName=domainOntology.owl
	domainOntologyNamespace=http\://ontologies/myontology\#
	domainOntologyFolder=.LexO-lite/
	```
1. Copy the empty lexicon file `mylexicon.owl` from `LexO-lite-master/lexicon` to `.LexO-lite/`. 

1. (OPTIONAL) If you did step 4, open the ontology you want to import with a text editor and change the Namespace to:
	```ini
	xmlns:ONTOLOGYNAME="http://ontologies/myontology#"
	```
	Where `ONTOLOGYNAME` is the name of your ontology.
	
	Save it as `domainOntology.owl` and copy it to `.LexO-lite`

1. Give Tomcat the rights to access and write to the .LexO-lite folder and the containing files.
	```bash
	chown -R tomcat:tomcat /.LexO-lite/ 
	```

1. Navigate to `LexO-lite-master/target/` and copy the *.war* file to `/var/lib/tomcat9/webapps/`

	```bash
	cp LexO-lite-1.0-SNAPSHOT.war /var/lib/tomcat9/webapps/
	```
1. (OPTIONAL) LexO will be  at `localhost:8080/NAME-OF-WAR-FILE`, so you can rename it however you like. In the next steps we will assume it is named `LexO-lite.war`
1. Start tomcat:
	```bash
	systemctl start tomcat
	```
1. Call the servlets for creating the database and populating it with the default admin user:
	```
	http://localhost:8080/LexO-lite/servlet/domainCreator?command=create
	```
	
	```
	http://localhost:8080/LexO-lite/servlet/domainCreator?command=preset
	```
	If everything went right, you should see the message `OK` when executing these commands.

1. Open LexO `http://localhost:8080/LexO-lite/` and log in with the default username and password `admin`.
