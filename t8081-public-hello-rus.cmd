chcp 65001
curl -vv -X POST -H "Content-Type: application/json" ^
-d "{\"key\": 144, \"stamp\":\"какая-то_строка+я\"}" ^
http://localhost:8082/api/public/hello-rus
