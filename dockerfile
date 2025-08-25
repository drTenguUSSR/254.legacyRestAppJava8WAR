# Этап сборки
FROM oracle/jdk:8 as builder

# Установка Gradle
RUN apt-get update && apt-get install -y unzip
RUN wget https://services.gradle.org/distributions/gradle-6.8.2-bin.zip
RUN unzip gradle-6.8.2-bin.zip -d /opt
RUN ln -s /opt/gradle-6.8.2/bin/gradle /usr/bin/gradle

# Копирование исходного кода
WORKDIR /app
COPY . .

# Сборка проекта
RUN gradle war

# Этап выполнения
FROM tomcat:8.5.63-jre8

# Удаляем стандартные приложения Tomcat для безопасности
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем собранное приложение
COPY --from=builder /app/build/libs/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Копируем кастомный server.xml
COPY conf/server.xml /usr/local/tomcat/conf/server.xml

# Создаем директорию для логов
RUN mkdir -p /usr/local/tomcat/logs

# Открываем порты
EXPOSE 8081
EXPOSE 8082

# Устанавливаем переменные среды по умолчанию
ENV SPECIAL_PORT=8082

# Запуск Tomcat
CMD ["catalina.sh", "run"]
