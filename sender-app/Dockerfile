FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/sender-app-*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]