# LegacyRestAppJava8WAR

demo. Приложение на Java 8 для разворачивания на Tomcat(WAR), используя старые библиотеки

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
