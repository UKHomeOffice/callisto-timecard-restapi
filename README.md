# Callisto REST API

## Running locally

### Github Package dependencies
In order to pull in Github package dependencies you will need a Github Personal Access Token.
This token will need the minimum of 'packages:read' permissions.

Update your .m2/settings.xml file to contain the <servers><server> tags like timecard_settings.xml
The token will need to live within your local .m2/settings.xml file as the password

Then run the following to build the project

```sh
$ mvn clean install
```

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


## Considerations
A `DRONE_TOKEN` is used in the drone yaml file to get access to execute drone cli commands. This token is tied to a specific user and stored in the drone secrets for this repo. If the user is removed from drone the `DRONE_TOKEN` must be replaced with someone else.


## Running Timecard-restapi locally as part of LocalDev environment.

1. Download LocalDev repository from https://github.com/UKHomeOffice/callisto-localdev and run it locally as described in Scenario 1.

2. From the LocalDev project root, stop Timecard-restapi service by running `docker compose stop timecard-restapi` command.

3. Pull Timecard-restapi repository and from its root directory, run command `docker compose up -d` 

After successful start, you should be able to work with Timecard-restapi code and all changes will be reflected within LocalDev environment.


### Devtools Hot Deployment in local environment

Devtools allows you to reload the application after making any changes to the project files. 
However, it may need stage of building project manually (InteliJ IDEA: Build/Build Project)
or  IntelliJ IDEA has 2 properties that will allow you to execute `Build Project` automatically. To enable that :
   1) Go to `Preferences/Build,Execution,Deployment/Compiler` and select option
      `Build project automatically`
   2) [Optional] Go to `Preferences/Advanced Settings` and select `Allow auto-make to start even if developed application is currently running`

## Naming Convention for keystore mountpath

We are currently using a persistent volume to create and store certificates in a keystore. The naming convention for the volume mounts is - 
```
<service_name>-keystore
```


