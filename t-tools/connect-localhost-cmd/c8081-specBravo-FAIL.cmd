echo must fail on call (HTTP/403)
curl -vv -X POST -H "Content-Type: application/json" ^
  -d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
  http://localhost:8081/api/special-bravo/mark
