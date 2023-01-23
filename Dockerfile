FROM openjdk:17-alpine
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
COPY ../../timecard-restapi-volume ../../tmp/tomcat-docbase.*
ENTRYPOINT ["java","-jar","timecard-api.jar"]
EXPOSE 9090