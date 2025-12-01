FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="oussema"

WORKDIR /app

COPY target/student-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
