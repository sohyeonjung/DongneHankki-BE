FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

COPY src/main/resources/application-prod.yml ./
COPY src/main/resources/dongnehankki-firebase-adminsdk-fbsvc-91e172f41b.json ./
COPY src/main/resources/googlecloudkey.json ./

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
