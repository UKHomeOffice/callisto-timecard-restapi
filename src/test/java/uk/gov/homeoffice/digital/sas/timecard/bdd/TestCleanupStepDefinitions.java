package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;

import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;

public class TestCleanupStepDefinitions<T extends BaseEntity> {

  private static final String SERVICE_NAME = "timecard";

  @After
  public void cleanup() {
    var jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    var objectMapper = new ObjectMapper();

    var tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    var admin = new Persona();
    admin.setTenantId(tenantId);

    var timeEntries =
            getCreatedResources("time-entries", jpaRestApiClient, objectMapper, admin);

    deleteResources("time-entries", jpaRestApiClient, admin, timeEntries);
  }

  @AfterAll
  public void cleanupAll() {
    var jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
    var objectMapper = new ObjectMapper();

    var tenantId = UUID.fromString(System.getProperty(TENANT_ID_SYSTEM_PROPERTY_NAME));
    var admin = new Persona();
    admin.setTenantId(tenantId);

    var timePeriodTypes = getCreatedResources("time-period-types", jpaRestApiClient, objectMapper, admin);

    deleteResources("time-period-types", jpaRestApiClient, admin, timePeriodTypes);
  }

  private T[] getCreatedResources(String resource, JpaRestApiClient jpaRestApiClient,
                                  ObjectMapper objectMapper, Persona admin) {
    var apiResponse = jpaRestApiClient.retrieve(
            admin,
            SERVICE_NAME,
            resource,
            null);

    var responseBody = apiResponse.getResponse().getBody().jsonPath().getMap("");
    return (T[]) objectMapper.convertValue(responseBody.get("items"), BaseEntity[].class );
  }

  private void deleteResources(String resource, JpaRestApiClient jpaRestApiClient, Persona admin,
                               T[] entities) {
    for (T entity : entities) {
      jpaRestApiClient.delete(
              admin,
              SERVICE_NAME,
              resource,
              entity.getId().toString());
    }
  }
}
