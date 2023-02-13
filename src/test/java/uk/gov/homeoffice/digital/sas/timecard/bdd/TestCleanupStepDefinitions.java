package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.HttpResponseManager;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiResourceResponse;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;

public class TestCleanupStepDefinitions {

  @After
  public static void cleanup() {
    JpaRestApiClient jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    HttpResponseManager httpResponseManager = new HttpResponseManager();
    ObjectMapper objectMapper = new ObjectMapper();

    UUID tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    Persona admin = new Persona();
    admin.setTenantId(tenantId);

    ArrayList timeEntries =
        getCreatedTimeEntries(jpaRestApiClient, httpResponseManager, objectMapper, admin);

    deleteTimeEntries(jpaRestApiClient, admin, timeEntries);

  }

  private static void deleteTimeEntries(JpaRestApiClient jpaRestApiClient, Persona admin,
                                ArrayList timeEntries) {
    for (int counter = 0; counter < timeEntries.size(); counter++) {
      System.out.println(timeEntries.get(counter));
      String id = (String) ((LinkedHashMap) timeEntries.get(counter)).get("id");

      jpaRestApiClient.delete(admin, "timecard", "time-entries", id);
    }
  }

  private static ArrayList getCreatedTimeEntries(JpaRestApiClient jpaRestApiClient,
                                        HttpResponseManager httpResponseManager,
                                        ObjectMapper objectMapper, Persona admin) {
    JpaRestApiResourceResponse
        apiResponse = jpaRestApiClient.retrieve(admin, "timecard", "time-entries", null);

    httpResponseManager.addResponse(apiResponse.getBaseResourceUri(),
        apiResponse.getResponse());

    var root = httpResponseManager.getLastResponse().getBody().jsonPath().getMap("");

    var testSubject = objectMapper.convertValue(root.get("items"), ArrayList.class);
    return testSubject;
  }
}
