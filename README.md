# LegacyRestAppJava8WAR

Demo. Приложение на Java 8 для разворачивания на Tomcat(WAR), используя старые библиотеки

## Концепция и зависимости (кратко)

Создать проект веб-приложения на java:

- базовый пакет для всего кода - mil.teng254.legacy
- Oracle Java 1.8 x64
- Gradle 6.8.2 + groovy
- Spring 5.2.8.Release
- SpringBoot не применяется
- используется Thymeleaf
- Jersey 1.19.4
- Google Guava 20.0
- Junit 4.12
- Log4j 1.2.14
- Lombok 1.18.20
- в web.xml параметр com.sun.jersey.api.json.POJOMappingFeature выключен (false)
- сериализация JSON через JAXB
- результат сборки - ROOT.war

[//]: # ([Технические подробности]&#40;docs-dev/tech-notes.md&#41;)

## M25903. Получение Http-заголовков вне контекста контролера. Работает 2-из-3

- Способ №1. alfa. рабочий. получение через ```RequestContextHolder```. для работы обязательно нужно устанавливать 
в потоке, где выполняется контролер с помощью ```RequestContextHolder.setRequestAttributes```
- Способ №2. bravo. рабочий. получение через ```javax.servlet.Filter```. извлечение - ```ThreadLocal<String>```.
  ```RequestContextHolder``` не используется
- Способ №3. kilo. рабочий. получение через ```javax.servlet.Filter```. извлечение - ```org.apache.logging.log4j.ThreadContext```.
```RequestContextHolder``` не используется

## M25902. thymeleaf и интеграционные тесты

- контролер special на ```JAXBElement<RequestDto>```
- добавлен рабочий thymeleaf
- интеграционные тесты:
  - с запуском сервера grizzly 1-й версии. Однопортовая конфигурация
  - тест для ```/public/hello-rest``` на дефолтном порту
  - тест для thymeleaf html странице ```/public/time```

## M25901. Контроль доступа через аннотацию

Ограничение доступа к специальному контролеру только
по определенному порту работает, НО полный перечень правок для
такой реализации составляет:

- контролер нужно аннотировать ```@SpecialPort```
- в конструкторе SpecialPortFilter нужно указать полный путь к контролеру
- шаблонные пути типа ```@Path("/{some:(?i:foo|bar)}/yoyo")``` не поддерживается
