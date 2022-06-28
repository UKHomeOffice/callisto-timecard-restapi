package uk.gov.homeoffice.digital.sas.timecard.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.server.LocalServerPort;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class HealthCheckStepDefinitions {

    private static final String LOCAL_HOST_PREFIX = "http://localhost:%d/%s";
    private static final String HEALTH_ENDPOINT = "/actuator/health";
    private static final String SERVICE_NAME = "callisto-timecard-service";

    private static String baseURI;
    private String status;

    @LocalServerPort
    private int port;

    @PostConstruct
    private void setApiEndPoint() {
        baseURI = String.format(LOCAL_HOST_PREFIX, port, SERVICE_NAME);
        RestAssured.baseURI = baseURI;
    }

    @Given("The Timecard service is running")
    public void microservice_is_started() {
        assertThat(this.port).isGreaterThan(1);
    }

    @When("I check the health status")
    public void check_health_status() {
        this.status = givenHealthEndpointResponse();
    }

    @Then("I will get the status as {string}")
    public void check_health_status(final String status) {
        assertThat(this.status).isEqualTo(status);
    }

    private static String givenHealthEndpointResponse() {
        Response response = getRunningService().when().get(HEALTH_ENDPOINT);
        return JsonPath.from(response.asString()).get("status");
    }

    private static RequestSpecification getRunningService() {
        return getRequestSpecification().contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    private static RequestSpecification getRequestSpecification() {
        Objects.requireNonNull(baseURI);
        final var requestSpecification = new RequestSpecBuilder().build();
        return RestAssured.given().spec(requestSpecification);
    }

}