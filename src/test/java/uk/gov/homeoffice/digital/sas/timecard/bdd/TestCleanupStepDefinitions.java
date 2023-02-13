package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.HttpResponseManager;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

public class TestCleanupStepDefinitions {

  @After
  public static void cleanup() {
    var jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    var httpResponseManager = new HttpResponseManager();
    var objectMapper = new ObjectMapper();

    var tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    var admin = new Persona();
    admin.setTenantId(tenantId);

    var timeEntries =
        getCreatedTimeEntries(jpaRestApiClient, httpResponseManager, objectMapper, admin);

    deleteTimeEntries(jpaRestApiClient, admin, timeEntries);

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

  private static TimeEntry[] getCreatedTimeEntries(JpaRestApiClient jpaRestApiClient,
                                                       HttpResponseManager httpResponseManager,
                                                       ObjectMapper objectMapper, Persona admin) {
    var apiResponse = jpaRestApiClient.retrieve(
        admin,
        "timecard",
        "time-entries",
        null);

    var responseBody = apiResponse.getResponse().getBody().jsonPath().getMap("");

    return objectMapper.convertValue(responseBody.get("items"), TimeEntry[].class);
  }
}
