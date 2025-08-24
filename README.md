# LegacyRestAppJava8WAR
demo. Приложение на Java 8 для развовачивания на Tomcat(WAR), используя старые библиотеки

## Концепция, идеи и ограничения

Создать проект веб-приложения на java:
- базовый пакет для всего кода - mil.teng254.legacy
- Oracle Java 1.8 x64
- Gradle 6.8.2 + groovy
- Spring 5.2.8.Release
- SpringBoot не применяется
- используется Thymeleaf
- Jersey 1.19.4 как реализация JAX-RS (JSR 311). зависимость com.sun.jersey:jersey-bundle:1.19.4
- Google Guava 20.0
- Junit 4.12
- Log4j 1.2.14 + xml конфигурация
- Lombok 1.18.20
- в web.xml параметр com.sun.jersey.api.json.POJOMappingFeature выключен (false)
- в web.xml используй 
    <listener>
        <listener-class>
            org.springframework.web.context.ContextLoaderListener
        </listener-class>
    </listener>
- результат сборки - ROOT.war

## Требования к функциям приложения:
- контролеры описываются и @RestController и @Path
- контролер POST /public/hello-path:\
  аннотирован @Path\
  принимает json:
```json
{"key": 100, "stamp":"2025-06-20T11:24:36Z"}
``` 
, где\
stamp - дата-время в формате ISO 8601\
  возвращает json:
```
{"res: 101, "stamp":"2025-06-20T10:24:36Z", "tz":"+03:00"}
```
,где\
101 - инкрементированное значение key типа long\
"stamp" - время, полученное из запроса, увеличенное на 1 час\
"tz" - временная зона сервера в виде смещения\
- контролер POST /public/hello-rest:
  аннотирован @RestController
  принимает json {"key": 100, "stamp":"2025-06-20T11:24:36Z"}
    stamp - дата-время в формате ISO 8601
  возвращает json {"res: 101, "stamp":"2025-06-20T10:24:36Z", "tz":"+03:00"}
    101 - инкрементированное значение key типа long
    "stamp" - время, полученное из запроса, увеличенное на 1 час
    "tz" - временная зона сервера в виде смещения
- контролер POST /special/mark-path обрабатывает запросы только на 8082 порту:
  аннотирован @Path
  принимает json {"key": 100, "stamp":"2025-06-20T11:24:36Z"}
    stamp - дата-время в формате ISO 8601
  возвращает json {"res: 101, "stamp":"2025-06-20T10:24:36Z", "tz":"+03:00"}
    101 - инкрементированное значение key типа long
    "stamp" - время, полученное из запроса, увеличенное на 1 час
    "tz" - временная зона сервера в виде смещения
- для ограничения порта обработки /special/mark-path
  ввести новую аннотицию @SpecialPortOnly(8082) которая работает так:
  если запрос приходит на порт отличный от указанного в аннотации параметра,
  запрос отвергается. вариант реализации - http-фильтр
- контролер POST обрабатывающий /hello с следующим описанием
        @Path("/hello")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public javax.ws.rs.core.Response processJson(JAXBElement<SimpleHello> data, @Context HttpServletRequest);

        @Data
        @XmlRootElement
        public class SimpleHello {
            private String name;
            private String mail;
        }

далее отдельно описать multistage сборку проекта
при этом используется:
- Oracle Java 1.8
- Apache Tomcat 8.5.63
- в Tomcat используется нестандартный concf/server.xml: 
  приложение прослушивает два порта HTTP 8081 и HTTP 8082

## AI.2025-08-24

Реализация мультипортового REST-контроллера в Spring 5 + Jersey 1.x

### Технологический стек
- Java 8
- Spring 5.2.8.RELEASE
- Jersey 1.19.4 (JAX-RS JSR 311)
- Tomcat (версии, совместимой с Java 8)
- Gradle 6.8.2 для сборки
- Log4j 1.2.14 для логирования

### Архитектурные решения
1. Конфигурация Tomcat
Добавлен дополнительный HTTP Connector в server.xml:

```xml
<Connector port="8081" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
```

2. Аннотация @SpecialPort
Маркерная аннотация для классов, требующих доступ только через специальный порт:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpecialPort {}
```

3. Динамическая конфигурация порта

Порт задается через переменную окружения SPECIAL_PORT:
- Если переменная не задана или некорректна — приложение не запускается
- Если значение -1 — функциональность отключена (все запросы блокируются)
- Любое другое число — номер порта для доступа

4. Проверка порта через ContainerRequestFilter\
Реализован фильтр SpecialPortFilter, который:
- При инициализации собирает все классы с аннотацией @SpecialPort в HashSet
- Для каждого запроса проверяет, должен ли он обрабатываться на специальном порту
- Возвращает 404 Not Found при нарушении правил доступа

5. Интеграция Spring и Jersey

Использован SpringServlet для автоматической регистрации компонентов и провайдеров.

6. Сериализация JSON через JAXB
Модели данных аннотированы JAXB аннотациями для преобразования в JSON и обратно.

### Ключевые компоненты

#### PortConfig.java

Загрузка и валидация настроек порта из переменных окружения.

#### SpecialPortFilter.java

Фильтр для проверки доступа к аннотированным ресурсам.

#### SpecialPortResource.java
Контроллер с аннотацией @SpecialPort, доступный только на специальном порту.

#### PublicResource.java
Обычный контроллер, доступный на всех портах.

Сборка и развертывание build.gradle
```gradle
plugins {
    id 'java'
    id 'war'
}

sourceCompatibility = '1.8'
targetCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.sun.jersey:jersey-bundle:1.19.4'
    implementation 'com.sun.jersey:jersey-spring:1.19.4'
    implementation 'org.springframework:spring-core:5.2.8.RELEASE'
    implementation 'org.springframework:spring-web:5.2.8.RELEASE'
    implementation 'org.springframework:spring-context:5.2.8.RELEASE'
    implementation 'org.springframework:spring-beans:5.2.8.RELEASE'
    implementation 'org.springframework:spring-aop:5.2.8.RELEASE'
    implementation 'org.springframework:spring-expression:5.2.8.RELEASE'
    implementation 'log4j:log4j:1.2.14'
    compileOnly 'javax.servlet:javax.servlet-api:3.1.0'
}

war {
    archiveFileName = 'jersey1-multi-port.war'
}
```

#### web.xml
Настроен SpringServlet и контекст Spring.

#### Запуск приложения
Установка переменной окружения
```bash
export SPECIAL_PORT=8081
```

#### Запуск Tomcat
Разместить WAR-файл в директории webapps и запустить Tomcat.

#### Проверка функциональности
Доступ к специальному эндпоинту на правильном порту
```bash
curl http://localhost:8081/special/data
```
Попытка доступа на неправильном порту
```bash
curl http://localhost:8080/special/data
# 404 Not Found
```

#### Доступ к публичному эндпоинту на любом порту
```bash
curl http://localhost:8080/public/info
curl http://localhost:8081/public/info
```

#### Отключение функциональности специального порта
```bash
export SPECIAL_PORT=-1
```

#### После перезапуска все запросы к /special вернут 404
Заключение
Реализована гибкая система разграничения доступа по портам с динамической конфигурацией.\ 
Приложение соответствует всем исходным требованиям и готово к развертыванию в Tomcat.

