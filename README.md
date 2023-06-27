# Callisto Timecard REST API

Callisto Timecard REST API is a part of Callisto project.
The best way to run the service is to leverage the LocalDev solution.

## Running as part of the LocalDev environment

1. Clone LocalDev repository from https://github.com/UKHomeOffice/callisto-localdev and run it locally as described in [Scenario 1](https://github.com/UKHomeOffice/callisto-localdev#scenario-1-running-callisto-without-need-to-edit-code-base-eg-demo-purposes).

2. From the LocalDev project root, stop the service by running `docker compose stop <service_name>` command.

3. Ensure you have configured your [Authentication for GitHub Packages](#authentication-for-github-packages)

4. Clone the service repository, and from its root directory run command `docker compose up -d`

After successful start, you should be able to work with the service code, and see all changes without restaring the docker container.

## Authentication for Github Packages

In order to pull in Github package dependencies you will need a Github Personal Access Token.
This token will need the minimum of 'packages:read' permissions.

Update your .m2/settings.xml file to contain the <servers><server> tags

```xml
<servers>
  <server>
      <id>github-packages</id>
      <username>DummyUser</username>
      <password>${env.GITHUB_TOKEN}</password>
  </server>
</servers>
```

For more info see:
[https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Then run the following to build the project

```sh
$ mvn clean install
```

## Debugging

Assuming you know how to debug java service, the principal is the same when using LocalDev solution.When running using docker compose, the debugger is exposed on the default port (5005). In the case of the debugger port conflicts, change both the ports and the entrypoint section in the docker-compose.yml

## Kafka

Project requires a Kafka instance with all required permissions to write to topic. If [running the service within LocalDev solution](#running-as-part-of-the-localdev-environment), no futher configuration is required.

All Kafka settings can be found in application.properties file.

## Database management

The database is configured to run on Postgres. Liquibase is used to manage database changesets. The docker-compose.yml ensures that the changesets are applied.

A separate docker image is created on top of the Liquibase image that contains all the changesets. This image is used to deploy the database to other environments.
