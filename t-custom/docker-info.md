# Сборка и запуск Docker образа

1. Сборка образа:
````bash
docker build -t legacy-java-app .
````
2. Запуск контейнера со стандартным портом:
````bash
docker run -d -p 8081:8081 -p 8082:8082 --name legacy-app legacy-java-app
````
3. Запуск контейнера с кастомным SPECIAL_PORT:
````bash
docker run -d -p 8081:8081 -p 8083:8083 -e SPECIAL_PORT=8083 --name legacy-app-custom legacy-java-app
````
4. Запуск контейнера с отключенной функциональностью специального порта:
````bash
docker run -d -p 8081:8081 -p 8082:8082 -e SPECIAL_PORT=-1 --name legacy-app-disabled legacy-java-app
````
