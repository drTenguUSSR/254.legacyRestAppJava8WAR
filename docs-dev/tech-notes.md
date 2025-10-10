# Технические заметки

- переделка в JAXB
было: com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl
стало: com.sun.xml.bind.v2.runtime.JAXBContextImpl
- добавлен тест на текущую реализацию JAXBContextImpl
- вывод warn в лог, если реализация неправильная (при старте приложения)
- в лог выводится полученный (raw) запрос
- механизм фильтрации по порту (SpecialPortFilter) работает. Проверка
t8081-spec-FAIL.cmd работает
- добавлена проверка с передачей сломанного JSON

Возможная донастройка tomcat для поддержки русского языка:

```xml
<Connector port="8080" protocol="HTTP/1.1"
URIEncoding="UTF-8"
connectionTimeout="20000"
redirectPort="8443" />
```
