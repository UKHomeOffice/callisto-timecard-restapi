package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import io.cucumber.java.BeforeAll;
import java.util.Map;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;

public class TestSetupStepDefinitions {


  @BeforeAll
  public static void setup() {
    UUID tenantId = UUID.randomUUID();
    System.setProperty(TENANT_ID_SYSTEM_PROPERTY_NAME, tenantId.toString());

    // TODO: get it from the system property
    Map<String, String> services = Map.of("timecard","http://localhost:9090");

    JpaRestApiClient jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry(services));
    Persona admin = new Persona();
    admin.setTenantId(tenantId);

    jpaRestApiClient.create(
        admin,
        "timecard",
        "time-period-types",
        "{\"name\":\"Shift\"}"
    );
  }
}
