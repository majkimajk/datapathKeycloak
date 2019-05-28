
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
2.Copy the whole catalogue "longerattributes" (from /resources/theme/) into {keycoloak_home}/themes

3.Build project
```
mvn clean package
```
4.Add module **keycloak-longer-attributes* (see)
```
{keycloak_home}/bin/jboss-cli.sh --command="module add --name=org.keycloak.keycloak-longer-attributes --resources=target/keycloak-longer-attributes-jar-with-dependencies.jar --dependencies=org.keycloak.keycloak-core,org.keycloak.keycloak-common,org.jboss.logging,org.keycloak.keycloak-services,org.keycloak.keycloak-model-jpa,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,javax.ws.rs.api,javax.persistence.api,org.hibernate,org.javassist,org.liquibase,javax.api,javax.annotation.api,javax.xml.ws.api,javax.jws.api,javax.servlet.api"
```

5. Start keycloak - as admin enable the extension (must be done per Realm) by choosing:
Realm Settings --> Themes --> Admin Console Theme --> choose "longerattributes" (see "enable theme.png")

6. The new tab "Longer Attributes" works the same as "Attributes", but it allows more characters. Underlying type is "text" therefore 65,535 characters are now allowed per attribute - the type can be easily changed if needed, via new liquibase changeset. (see "longer attributes.png")