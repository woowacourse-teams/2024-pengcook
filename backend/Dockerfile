FROM openjdk:21

ARG JARFILE=build/libs/*.jar

ENV PROFILE=dev

COPY ${JARFILE} /app.jar

ENTRYPOINT ["java", "-jar"]
CMD ["-Dspring.profiles.active=${PROFILE}", "/app.jar"]