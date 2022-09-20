FROM openjdk:17-alpine
RUN apk --no-cache add postgresql-client
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT ["java","-jar","timecard-api.jar"]
EXPOSE 9090