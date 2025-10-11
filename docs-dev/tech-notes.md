# Технические заметки

Возможная донастройка tomcat для поддержки русского языка:

## резерв

```xml
<Connector port="8080" protocol="HTTP/1.1"
URIEncoding="UTF-8"
connectionTimeout="20000"
redirectPort="8443" />
```
