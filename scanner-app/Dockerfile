FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/scanner-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
