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
bin\startup.bat
````
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
