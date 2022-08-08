FROM liquibase/liquibase:4.15.0
WORKDIR /liquibase
COPY changelog changelog
COPY sql sql
#ENTRYPOINT ["java","-jar","timecard-api.jar"]


#docker run -it -rm --url="jdbc:postgresql://[YOUR IP ADDRESS]:5432/callisto" --changeLogFile=changelog/db.changelog-main.yml --username=postgres --password=Welcome update
