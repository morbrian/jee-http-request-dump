To view the output, deploy the WAR artifact and view this URL:

        http://localhost:8080/http-request-dump/inspect

Similar information should also be available in the server logs.

## JAAS

Explore setting the Principal object and enforcing policies with JAAS using a container,
such as TomEE. [TomEE JASS Reference](https://tomee.apache.org/tomee-jaas.html)

### PropertiesLoginModule TomEE JAAS Configuration

Add to your CATALINA_OPTS the java.security.auth.login.config system property:

``
-Djava.security.auth.login.config=$CATALINA_BASE/conf/login.config
``

Configure your realm in server.xml file


        <?xml version='1.0' encoding='utf-8'?>
        <Server port="8005" shutdown="SHUTDOWN">
          <Listener className="org.apache.tomee.loader.OpenEJBListener" />
          <Listener className="org.apache.catalina.security.SecurityListener" />
        
          <Service name="Catalina">
            <Connector port="8080" protocol="HTTP/1.1" 
                       connectionTimeout="20000" 
                       redirectPort="8443" />
            <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
            <Engine name="Catalina" defaultHost="localhost">
              <!-- here is the magic -->
              <Realm className="org.apache.catalina.realm.JAASRealm" appName="PropertiesLoginModule"
                  userClassNames="org.apache.openejb.core.security.AbstractSecurityService$User"
                  roleClassNames="org.apache.openejb.core.security.AbstractSecurityService$Group">
              </Realm>
        
              <Host name="localhost"  appBase="webapps"
                    unpackWARs="true" autoDeploy="true" />
            </Engine>
          </Service>
        </Server>


Configure your login.config file

        PropertiesLogin {
            org.apache.openejb.core.security.jaas.PropertiesLoginModule required
            Debug=false
            UsersFile="users.properties"
            GroupsFile="groups.properties";
        };


Configure your login module specifically (users.properties for snippets of this page for instance)

Example group.properties:

        admin=dev_moore
        reader=cristina,dev_sanders,dev_clinton
        
Example users.properties:

        cristina=changeme
        dev_clinton=changeme
        dev_sanders=changeme
        dev_moore=changeme

### CLIENT-CERT TomEE JAAS Configuration

A user Principal may be produced when using SSL client certificates if the `auth-method` is specfied
as `CLIENT-CERT. The one caveat is the full LDFI must be specified in the user database, and not
just the CN portion.

For Example, if using a `conf/tomcat-users.xml` file:

          <role rolename="reader"/>
          <user username="CN=dev_Hillary_Clinton, OU=dev_Hillary_Clinton, O=dev_Hillary_Clinton, L=San Diego, ST=CA, C=US" password="" roles="reader" />

Assuming the tomcat server is already configured for SSL connections in the `server.xml`, the rest
of the configuration is performed in the web.xml of the application.

The `security-constraint` below protects the entire web application, requiring all users to be
authenticated using a `CLIENT-CERT` and to be in the role `reader`.

        <security-constraint>
            <web-resource-collection>
                <web-resource-name>SimpleRequestInspectorServlet</web-resource-name>
                <url-pattern>/*</url-pattern>
            </web-resource-collection>
            <auth-constraint>
                <!-- must specify roles: if no roles are specified, no principal is produced -->
                <role-name>reader</role-name>
            </auth-constraint>
            <user-data-constraint>
                <transport-guarantee>CONFIDENTIAL</transport-guarantee>
            </user-data-constraint>
        </security-constraint>
    
        <login-config>
            <auth-method>CLIENT-CERT</auth-method>
        </login-config>
        
HTTP Method specific constraintes may be specfied by adding `http-method` elements to the 
`web-resource-collection`

        <http-method>GET</http-method>
        
The `JAASRealm` must be added to the `conf/server.xml` with appropriate user and role classes specified,
as well as an associated configuration file. Any properly configured `LoginModule` will work with
the `JAASRealm`, such as the `DefaultUserBaseRealm` that uses the tomee-users.xml file. However,
when using `CLIENT-CERT` as the `auth-method`, the `PropertiesLoginModule` described above is
not ideal because the certificate `subject` names are likely to include the `=` character which
conflicts with the property file format syntax.

Here is an example of configuring an `SQLLoginModule` available in TomEE installations:

        <Realm className="org.apache.catalina.realm.JAASRealm" appName="SqlLoginModule"
              userClassNames="org.apache.openejb.core.security.jaas.UserPrincipal"
              roleClassNames="org.apache.openejb.core.security.jaas.GroupPrincipal"
        /> 

The associated configuration file would look something like this:

        SqlLoginModule {
            org.apache.openejb.core.security.jaas.SQLLoginModule required 
            dataSourceName="DocumentDS"
            userSelect="select username, password from users where username=?"
            groupSelect="select username, groupname from users, user_group_relation, groups where username=? and users.id=user_group_relation.user_id and groups.id=user_group_relation.group_id";
        };

## Troubleshooting

1) The SQLLoginModule is pretty good about logging configuration errors, but if you are curious 
about the usernames or passwords it is pulling from the database, you can copy it into your own
repository from the [TomEE GitHub Repository](https://github.com/apache/tomee.git). It will require
a few `provided` scope packages to compile. Specify the package name of your version of it in the 
sql-login.config file so it will load with any additional logging statements you add.

        <dependency>
            <groupId>org.apache.openejb</groupId>
            <artifactId>openejb-core</artifactId>
            <version>4.7.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.openejb</groupId>
            <artifactId>openejb-loader</artifactId>
            <version>4.7.1</version>
            <scope>provided</scope>
        </dependency>

2) The `JAASRealm` class has reasonably good logging, but you might have too look at the source
code to figure out exactly what to fix. The [Tomcat GitHub Repository](https://github.com/apache/tomcat.git)
has the source. Otherwise, just enable logging for these packages.

This example shows modifying `conf/logging.properties`

        org.apache.catalina.authenticator.level = FINE
        org.apache.catalina.authenticator.handlers = 2localhost.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler
        
        org.apache.catalina.realm.level = FINE
        org.apache.catalina.realm.handlers = 2localhost.org.apache.juli.FileHandler, java.util.logging.ConsoleHandler

