FROM openjdk:17-alpine
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT___ ["java","-jar","timecard-api.jar"]
EXPOSE 8081