# Тестирование приложения

После запуска контейнера можно протестировать endpoints с помощью curl:

## Тестирование /public/hello-path и /public/hello-rest

````bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"key": 100, "stamp":"2025-06-20T11:24:36Z"}' \
  http://localhost:8081/api/public/hello-path

curl -X POST -H "Content-Type: application/json" \
  -d '{"key": 100, "stamp":"2025-06-20T11:24:36Z"}' \
  http://localhost:8081/api/public/hello-rest
````

## Тестирование /special/mark-path

````bash
# Этот запрос должен работать на порту, указанном в SPECIAL_PORT
curl -X POST -H "Content-Type: application/json" \
  -d '{"key": 100, "stamp":"2025-06-20T11:24:36Z"}' \
  http://localhost:8082/api/special/mark-path

# Этот запрос должен вернуть ошибку 403
curl -X POST -H "Content-Type: application/json" \
  -d '{"key": 100, "stamp":"2025-06-20T11:24:36Z"}' \
  http://localhost:8081/api/special/mark-path
````
