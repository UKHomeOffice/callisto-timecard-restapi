# Callisto REST API

## Running locally

### Create database

If you need to create your own postgres database you can use docker. This works well if you create a container and name it, then you can stop and start it as you please

```sh
docker create --name postgres-local -e POSTGRES_PASSWORD=Welcome -p 5432:5432 postgres:11.5-alpine
docker start postgres-local
```

Connect to database using `psql`

```sh
psql -h [YOUR DOCKER HOST IP ADDRESS] -U postgres
```

For example if `echo $DOCKER_HOST` returns `tcp://192.168.64.4:2376` then run `psql -h 192.168.64.4 -U postgres`

If the above echo command doesn't work try:
```sh
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' postgres-local
```
Create the database
```sql
CREATE DATABASE callisto;
```

### Setting environment variables 

Before running the database locally, you need to set your local environment variables e.g.

```
DATABASE_ENDPOINT=localhost
DATABASE_NAME=callisto
DATABASE_PORT=5432
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=Welcome
```

## Updating the database schema

The following command will deploy the liquibase config using the docker image when run from the root of the project.
You will need to replace the value [YOUR IP ADDRESS] with the IP address for your database. If you are not running on the default port you may also need to update that in the connection string. Check that the username and password are also correct

```sh
docker run -it --rm -v $(pwd)/db/changelog:/liquibase/changelog -v $(pwd)/db/sql:/liquibase/sql liquibase/liquibase  --url="jdbc:postgresql://[YOUR IP ADDRESS]:5432/callisto" --changeLogFile=changelog/db.changelog-main.yml --username=postgres --password=Welcome update
```