# Cookbook

## Базовый промпт для генерации кода

- используется Oracle Java 1.8 x64, Spring 5.2.8.Release, Jersey 1.19.4, Gradle 6.8.2 + groovy, JUnit 4.12.
- исполльзуется Lombok 1.18.20, Google Guava 20.0, Log4j 1.2.14 + xml конфигурация, фасад slf4j
- SpringBoot не используется
- в `web.xml` параметр `com.sun.jersey.api.json.POJOMappingFeature` выключен (false)
- сериализация JSON через JAXB
- используй SpringServlet для автоматической регистрации компонентов и провайдеров.
- для интеграции Jersey и Spring используется
  `com.sun.jersey.spi.spring.container.servlet.SpringServlet`.
- параметры контролеров обернуты в JAXBElement
- контролеры аннотированы `@Consumes(MediaType.APPLICATION_JSON)`
  и `@Produces(MediaType.APPLICATION_JSON)`
- контролеры возвращают тип `javax.ws.rs.core.Response`;
- все типы данных, используемые как параметры контроллера аннотированы @XmlRootElement
- все типы данных, используемые внутри javax.ws.rs.core.Response возвращаемые контроллерами, аннотированы @XmlRootElement
- ObjectFactory не применяется;
- JAXBElement провайдер не применяется (com.sun.jersey.json.impl.JAXBElementProvider)

## Цель применения JAXBElement&lt;RequestDto&gt;

Использование JAXBElement<RequestDto> request необходимо в более сложных
случаях, когда информации в аннотациях самого класса недостаточно для
однозначного преобразования.

Основные ситуации, требующие JAXBElement:
1. Сложная схема XML: Элемент в схеме одновременно обладает свойствами
   nillable="true" и minOccurs="0". JAXBElement позволяет четко различать случай,
   когда элемент отсутствует (значение null), и случай, когда элемент присутствует с атрибутом xsi:nil="true".

2. Несколько элементов с одним типом: Когда в схеме существует несколько
   глобальных XML-элементов, соответствующих одному Java-типу. JAXBElement
   сохраняет информацию о том, какой именно элемент был прочитан из XML.

3. Отсутствие аннотации @XmlRootElement: Если класс не помечен этой аннотацией
   (например, когда классы генерируются из XSD-схемы), то при маршалинге (преобразовании
   объекта в XML/JSON) может потребоваться JAXBElement, чтобы явно задать
   имя корневого элемента

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
