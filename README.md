# Callisto REST API

## Running locally

Before running the application locally in the form of a Docker container, any already running instances of the application (also in the form of a Docker container) should be stopped to avoid port conflicts, unless
other ports have been assigned to each application instance. 

To run the application in the form of a Docker container, run the command `docker compose up -d` from the root of the application directory.

The database engine containing the `timecard schema` should be running already.

#### Flow

First, Docker Compose will run the container containing Liquibase, which will perform any necessary DB migrations. This container will be killed automatically. The container logs contain a message about the success/failure of the migration execution.

Then the timecard-restapi application in the form of a container will be launched, but it will be possible to edit the application code. Changes will be visible almost automatically thanks to hot-reload.

**Restarting the container** after making changes to the application or building its new image is not necessary or advisable.


### Devtools Hot Deployment in local environment 

Devtools allows you to reload the application after changing the state of the file, but to see the effect of the changes you need to do one of the following options:

 - 1 - Build project manually (InteliJ IDEA: Build/Build Project )
 - 2 - [or] IntelliJ IDEA has 2 properties that will allow you to execute `Build Project` automatically. 
   1) Go to `Preferences/Build,Execution,Deployment/Compiler` and select option
      `Build project automatically`
   2) [Optional] Go to `Preferences/Advanced Settings` and select `Allow auto-make to start even if developed application is currently running`



### Authenticate with the Home Office private Maven repository (Artifactory)

In order to retrieve private Maven packages, you’ll need to [configure authentication for Artifactory](https://collaboration.homeoffice.gov.uk/display/EAHW/Artifactory).

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