FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

COPY src/main/resources/application-prod.yml ./

# Firebase 및 Google Cloud 키 파일은 클래스패스 리소스이므로 BOOT-INF/classes/로 복사
COPY src/main/resources/dongnehankki-firebase-adminsdk-fbsvc-91e172f41b.json BOOT-INF/classes/
COPY src/main/resources/googlecloudkey.json BOOT-INF/classes/

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
