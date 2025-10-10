# LegacyRestAppJava8WAR

Demo. Приложение на Java 8 для разворачивания на Tomcat(WAR), используя старые библиотеки

## Концепция и зависимости (кратко)

Создать проект веб-приложения на java:

- базовый пакет для всего кода - mil.teng254.legacy
- Oracle Java 1.8 x64
- Gradle 6.8.2 + groovy
- Spring 5.2.8.Release
- SpringBoot не применяется
- используется Thymeleaf 3.0.15.RELEASE
- Jersey 1.19.4
- Google Guava 20.0
- Junit 4.12
- log4j-api:2.4 + SLF4J (log2j2.xml)
- Lombok 1.18.20
- JAXB API 2.3.1
- в web.xml параметр com.sun.jersey.api.json.POJOMappingFeature выключен (false)
- сериализация JSON через JAXB(jersey-json)
- результат сборки - ROOT.war

[Технические подробности](docs-dev/tech-notes.md)

## M25A02. Описание+оптимизация применения JAXB. Логирование сырого запроса

- переделка в JAXB
  было: `com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl`
  стало: `com.sun.xml.bind.v2.runtime.JAXBContextImpl`
- добавлен тест на текущую реализацию JAXBContextImpl
- вывод warn в лог, если реализация JAXB неправильная (при старте приложения)
- в лог выводится полученный (raw) запрос - для последующей внешней проверки
- добавлена cmd-вызов с передачей сломанного JSON
- подробно описана конвертация http/text в DTO см. [подробности docs-dev/http2dto-info.md](docs-dev/http2dto-info.md)

## M25A01. Http-заголовки (3-из-3). Фильтр javax.servlet.Filter для перезаписи RequestContextHolder

- работают все три способа передачи (см. ниже)
- интеграционный тест для передачи русского текста в рамках POST запрос/ответ
- cmd-скрипт для теста русского языка из windows консоли
- логирование порта сервера, куда пришел запрос
- получение http-заголовков, если код, в котором он нужен,
  не может получить заголовок как параметр метода (три варианта - alfa, bravo, kilo):
  - alfa - использует RequestContextHolder.currentRequestAttributes
  - bravo - использует ThreadLocal
  - kilo - использует ThreadContext
- интеграционный тест на все три (alfa, bravo, kilo) способа передачи HTTP-заголовков
- работа теста с LocalDate. передача в виде строки, парсинг в JAXB из произвольного
 формата в LocalDate ; возврат в виде строки

## M25903. Получение Http-заголовков вне контекста контролера. Работает 2-из-3

- Способ №1. alfa. рабочий. получение через ```RequestContextHolder```. для работы
 обязательно нужно устанавливать в потоке, где выполняется контролер с помощью
 ```RequestContextHolder.setRequestAttributes```
- Способ №2. bravo. рабочий. получение через ```javax.servlet.Filter```.
 извлечение - ```ThreadLocal<String>```.
 ```RequestContextHolder``` не используется
- Способ №3. kilo. рабочий. получение через ```javax.servlet.Filter```.
 извлечение - ```org.apache.logging.log4j.ThreadContext```.
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
