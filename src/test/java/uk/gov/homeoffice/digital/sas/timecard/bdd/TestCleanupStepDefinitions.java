package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.model.TimePeriodType;

public class TestCleanupStepDefinitions {

  @After
  public static void cleanup() {
    var jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    var objectMapper = new ObjectMapper();

    var tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    var admin = new Persona();
    admin.setTenantId(tenantId);

    var timeEntries =
        getCreatedTimeEntries(jpaRestApiClient, objectMapper, admin);

    deleteTimeEntries(jpaRestApiClient, admin, timeEntries);
  }

  @AfterAll
  public static void cleanupAll() {
    var jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    var objectMapper = new ObjectMapper();

    var tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    var admin = new Persona();
    admin.setTenantId(tenantId);

    var timePeriodTypes = getCreatedTimePeriodTypes(jpaRestApiClient, objectMapper, admin);

    deleteTimePeriodTypes(jpaRestApiClient, admin, timePeriodTypes);
  }

  private static TimeEntry[] getCreatedTimeEntries(JpaRestApiClient jpaRestApiClient,
                                                       ObjectMapper objectMapper, Persona admin) {
    var apiResponse = jpaRestApiClient.retrieve(
        admin,
        "timecard",
        "time-entries",
        null);

    var responseBody = apiResponse.getResponse().getBody().jsonPath().getMap("");

    return objectMapper.convertValue(responseBody.get("items"), TimeEntry[].class);
  }

  private static TimePeriodType[] getCreatedTimePeriodTypes(JpaRestApiClient jpaRestApiClient,
                                                            ObjectMapper objectMapper, Persona admin) {
    var apiResponse = jpaRestApiClient.retrieve(
        admin,
        "timecard",
        "time-period-types",
        null);

    var responseBody = apiResponse.getResponse().getBody().jsonPath().getMap("");

    return objectMapper.convertValue(responseBody.get("items"), TimePeriodType[].class);
  }

  private static void deleteTimeEntries(JpaRestApiClient jpaRestApiClient, Persona admin,
                                        TimeEntry[] timeEntries) {
    for (TimeEntry timeEntry : timeEntries) {
      jpaRestApiClient.delete(
          admin,
          "timecard",
          "time-entries",
          timeEntry.getId().toString());
    }
  }

  private static void deleteTimePeriodTypes(JpaRestApiClient jpaRestApiClient, Persona admin,
                                        TimePeriodType[] timePeriodTypes) {
    for (TimePeriodType timePeriodType : timePeriodTypes) {
      jpaRestApiClient.delete(
          admin,
          "timecard",
          "time-period-types",
          timePeriodType.getId().toString());
    }
  }
}
