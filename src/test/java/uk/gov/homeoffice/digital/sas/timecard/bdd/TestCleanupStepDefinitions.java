package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import java.util.ArrayList;
import java.util.Iterator;
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

    UUID tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    JpaRestApiClient jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    Persona admin = new Persona();
    admin.setTenantId(tenantId);


    HttpResponseManager httpResponseManager = new HttpResponseManager();
    ObjectMapper objectMapper = new ObjectMapper();

    JpaRestApiResourceResponse
        apiResponse = jpaRestApiClient.retrieve(admin, "timecard", "time-entries", null);

    httpResponseManager.addResponse(apiResponse.getBaseResourceUri(),
        apiResponse.getResponse());

    String body = httpResponseManager.getLastResponse().body().asString();

    var root = httpResponseManager.getLastResponse().getBody().jsonPath().getMap("");
    body.length();

    var testSubject = objectMapper.convertValue(root.get("items"), ArrayList.class);
    for (int counter = 0; counter < testSubject.size(); counter++) {
      System.out.println(testSubject.get(counter));
      String id = (String) ((LinkedHashMap) testSubject.get(counter)).get("id");

      jpaRestApiClient.delete(admin, "timecard", "time-entries", id);
      System.out.println(id);
    }

  }
}
