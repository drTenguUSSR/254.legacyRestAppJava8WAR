# Технические заметки

- переделка в JAXB 
было: com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl
стало: com.sun.xml.bind.v2.runtime.JAXBContextImpl
- написать тест на текущую реализацию JAXBContextImpl
- написать вывод warn в лог, если реализация неправильная
- в лог выводится полученный (raw) запрос. статус работоспособности: тест - FAIL, tomcat - ?
- механизм фильтрации по порту (SpecialPortFilter). статус работоспособности: тест - FAIL, tomcat - ?
- проверка t8081-spec-FAIL.cmd не работает

Возможная донастройка tomcat для поддержки русского языка:
```xml
<Connector port="8080" protocol="HTTP/1.1"
URIEncoding="UTF-8"
connectionTimeout="20000"
redirectPort="8443" />
```
