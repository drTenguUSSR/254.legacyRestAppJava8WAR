# Cookbook

## Переустановка используемой версии gradlew

````cmd
gradlew wrapper --gradle-version 6.8.2
````

## Запуск в tomcat

- новый файл go-start.cmd
````bat
set JAVA_OPTS=%JAVA_OPTS% -Xms1024m -Xmx2096m -Dfile.encoding=utf-8
set JAVA_OPTS=%JAVA_OPTS% -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
@rem  -Xdebug

set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=localhost:5006
set JPDA_SUSPEND=n

call bin\catalina.bat jpda start
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
<Server>
        <Connector port="${MAIN_PORT}" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />

        <Connector port="${SPECIAL_PORT}" protocol="HTTP/1.1"
                   connectionTimeout="20000"
                   redirectPort="8443" />
</Server>
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

## Запуск отладки в Intellij Idea

- run/debug configurations

      Debugger mode: attach to remote JVM
      Transport: Socket
      Host: localhost
      Port: 5006
      JDK 5-8
      Command line: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006
