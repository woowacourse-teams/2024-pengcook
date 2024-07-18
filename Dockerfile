
FROM --platform=linux/arm64 openjdk:21

ARG JAR_FILE=backend/build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

