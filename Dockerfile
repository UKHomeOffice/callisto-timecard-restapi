FROM openjdk:17-alpine
RUN apk add --no-cache \
    bash=5.1.16-r0 \
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT ["java","-jar","timecard-api.jar"]
EXPOSE 9090
