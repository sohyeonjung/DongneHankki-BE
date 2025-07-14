FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

COPY src/main/resources/application-prod.yml src/main/resources/application-prod.yml

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
