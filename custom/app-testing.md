# Тестирование приложения

После запуска контейнера можно протестировать endpoints с помощью curl:

## Тестирование /public/hello-path и /public/hello-rest

t-check-public-hello.cmd
````cmd
curl -vv -X POST -H "Content-Type: application/json" ^
  -d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
  http://localhost:8081/api/public/hello-path
````
ответ успешный

t-check-public-rest.cmd
````cmd
curl -vv -X POST -H "Content-Type: application/json" ^
  -d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
  http://localhost:8081/api/public/hello-rest
````
ответ успешный

## Тестирование /special/mark-path

Этот запрос должен работать на порту, указанном в SPECIAL_PORT
t-check-8082-special.cmd
````cmd
curl -vv -X POST -H "Content-Type: application/json" ^
  -d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
  http://localhost:8082/api/special/mark-path
````

Этот запрос должен вернуть ошибку 403
t-check-8081-special.cmd
````cmd
curl -vv -X POST -H "Content-Type: application/json" ^
  -d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
  http://localhost:8081/api/special/mark-path
````
