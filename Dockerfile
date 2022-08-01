FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT ["java","-jar","/timecard-api.jar"]
EXPOSE 8081