
## Installation

1.Modify add provider to standalone.xml
Inside tag
```
    <subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">
```
Modify **providers** tag (add module *module:org.keycloak.keycloak-longer-attributes*)
```
            <providers>
                <provider>classpath:${jboss.home.dir}/providers/*</provider>
                <provider>module:org.keycloak.keycloak-longer-attributes</provider>
            </providers>
```
2.Modify datasource *KeycloakDS* in standalone.xml
Inside
```
    <subsystem xmlns="urn:jboss:domain:datasources:5.0">

```
Modify datasource
```$xslt
                <datasource jndi-name="java:jboss/datasources/KeycloakDS" pool-name="KeycloakDS" enabled="true" use-java-context="true">
                    <connection-url>jjdbc:mysql://localhost:3306/datapath_db?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC&autoReconnect=true</connection-url>
                    <connection-property name="autoCommit">
                        true
                    </connection-property>
                    <driver>mysql</driver>
                    <pool>
                        <max-pool-size>20</max-pool-size>
                    </pool>
                    <security>
                        <user-name>root</user-name>
                        <password>secret</password>
                    </security>
                </datasource>
                
```
And add mysql provider (which is installed later).  Add new **<driver>** tag in **<drivers>** 
```
                <drivers>
                    <driver name="mysql" module="com.mysql">
                        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
                    </driver>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                </drivers>
```

3.Install dependencies
```
    C:/keycloak/keycloak-4.8.3.Final/bin/jboss-cli.sh --command="module add --name=com.mysql --resources=mysql-connector-java-5.1.38.jar --dependencies=javax.api"
```
4.Build project
```
mvn clean package
```
5.Add module **keycloak-longer-attributes* (see)
```
C:/keycloak/keycloak-4.8.3.Final/bin/jboss-cli.sh --command="module add --name=org.keycloak.keycloak-longer-attributes --resources=target/keycloak-longer-attributes-jar-with-dependencies.jar --dependencies=org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-model-jpa,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,javax.ws.rs.api,javax.persistence.api,org.hibernate,org.javassist,org.liquibase"
```


## DEV DEPLOYMENTS:

Please note that if you want to use **deploy.sh** and **undeploy.sh** you need to configure additional script.

Example:
**get-keycloak-home.sh**

```
#!/usr/bin/env bash

echo '/home/user/tools/UMM1.0/keycloak-3.4.3.Final/'

```