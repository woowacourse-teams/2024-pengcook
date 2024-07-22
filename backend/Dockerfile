FROM openjdk:21

ARG JARFILE=build/libs/*.jar
ARG PROFILE=dev

COPY ${JARFILE} /app.jar

ENTRYPOINT java -jar -Dspring.profiles.active=${PROFILE} /app.jar