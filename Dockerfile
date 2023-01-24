FROM openjdk:17-alpine
RUN apk add --no-cache \
    curl=7.79.1-r4 \
    bash=5.1.16-r0 \
    openssl=1.1.1s-r0 \
    jq=1.6-r1 \
    aws-cli=1.19.93-r0 &&\
    mkdir /.aws &&\
    chown 1001 /.aws &&\
    chgrp 1001 /.aws
WORKDIR /usr/src/main
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timecard-api.jar
ENTRYPOINT ["java","-jar","timecard-api.jar"]
EXPOSE 9090