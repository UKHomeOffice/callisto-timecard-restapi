FROM openjdk:17-alpine
RUN apk add --no-cache \
    bash=5.1.16-r0 \
RUN wget -q http://mirror.vorboss.net/apache/kafka/3.3.2/kafka_2.13-3.3.2.tgz -O - | tar -xzf -; mv kafka_2.13-3.3.2 /kafka
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT ["java","-jar","timecard-api.jar"]
EXPOSE 9090
