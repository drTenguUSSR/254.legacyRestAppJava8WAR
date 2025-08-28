# Cookbook

## Переустановка используемой версии gradlew

````cmd
gradlew wrapper --gradle-version 6.8.2
````

## запуск в tomcat

- новый файл go-start.cmd
````bat
set MAIN_PORT=8081
set SPECIAL_PORT=8082
echo ports main:%MAIN_PORT% spec:%SPECIAL_PORT%
set JAVA_OPTS=-Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource
del logs\*.log
del logs\*.txt
call x-set-1.8.cmd
bin\startup.bat
````
альтернатива - указать org.apache.tomcat.util.digester.PROPERTY_SOURCE в
conf\catalina.properties

- новый файл go-stop.cmd
````bat
bin\shutdown.bat
````

- правка существующего файла
conf/server.xml
````xml
        <Connector port="${MAIN_PORT}" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />

        <Connector port="${SPECIAL_PORT}" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />
````

- ошибка - ```Caused by: java.lang.ClassNotFoundException: javax.xml.bind.JAXBContext```
для запуска на версиях Java 9+ требуется правка gradle.build

````groovy
dependencies {
    // Другие зависимости...
    
    // JAXB API
    implementation 'javax.xml.bind:jaxb-api:2.3.1'
    
    // JAXB Implementation (GlassFish RI)
    implementation 'org.glassfish.jaxb:jaxb-runtime:2.3.1'
    
    // Активация (требуется для JAXB в версиях до 2.3.0)
    implementation 'javax.activation:activation:1.1.1'
}
````