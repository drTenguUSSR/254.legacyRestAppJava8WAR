curl -vv -X POST -H "Content-Type: application/json" ^
-H "X-Cust-Alfa: AA01" ^
-H "X-Cust-Bravo: BB02" ^
-H "X-Cust-Kilo: KK03" ^
-d "{\"key\": 100, \"stamp\":\"2025-06-20T11:24:36Z\"}" ^
http://localhost:8082/api/public/hello-rest
