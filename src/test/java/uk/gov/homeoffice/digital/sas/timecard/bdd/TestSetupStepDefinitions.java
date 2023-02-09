package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager.TENANT_ID_SYSTEM_PROPERTY_NAME;

import io.cucumber.java.Before;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.ServiceRegistry;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;

public class TestSetupStepDefinitions {

  @Before
  public static void setup() {
    UUID tenantId = UUID.randomUUID();
    System.setProperty(TENANT_ID_SYSTEM_PROPERTY_NAME, tenantId.toString());

    // Instantiating the API client bean explicitly. Autowiring it from Spring context doesn't work
    // in cucumber @BeforeAll
    JpaRestApiClient jpaRestApiClient = new JpaRestApiClient(new ServiceRegistry());
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
