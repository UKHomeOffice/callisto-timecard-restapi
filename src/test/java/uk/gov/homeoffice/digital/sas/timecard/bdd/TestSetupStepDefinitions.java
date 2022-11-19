package uk.gov.homeoffice.digital.sas.timecard.bdd;

import static uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient.TENANT_ID_SYSTEM_PROPERTY_NAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiClient;
import uk.gov.homeoffice.digital.sas.cucumberjparest.api.JpaRestApiResourceResponse;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.Persona;
import uk.gov.homeoffice.digital.sas.cucumberjparest.persona.PersonaManager;
import uk.gov.homeoffice.digital.sas.timecard.model.TimePeriodType;

public class TestSetupStepDefinitions {

  private UUID tenantId;

  private final Map<String, String> sharedVariables;

  private final ApplicationContext context;

  public TestSetupStepDefinitions(Map<String, String> sharedVariables, ApplicationContext context) {
    this.sharedVariables = sharedVariables;
    this.context = context;
  }

  @Given("a shift time period type")
  public void insertTimePeriodType() throws JsonProcessingException {
    if (tenantId == null) {
      tenantId = UUID.randomUUID();
      System.setProperty(TENANT_ID_SYSTEM_PROPERTY_NAME, tenantId.toString());

      JpaRestApiClient jpaRestApiClient = context.getBean(JpaRestApiClient.class);
      PersonaManager personaManager = context.getBean(PersonaManager.class);
      ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
      Persona admin = personaManager.createPersona("admin");

      TimePeriodType shiftType = new TimePeriodType();
      shiftType.setName("Shift");

      JpaRestApiResourceResponse response =
          jpaRestApiClient.create(admin, tenantId.toString(), "timecard", "time-period-types",
              objectMapper.writeValueAsString(shiftType));
      TimePeriodType persistedTimePeriodType = response.getResponse().getBody()
          .jsonPath().getObject("items[0]", TimePeriodType.class);

      sharedVariables.put("timePeriodTypeId", persistedTimePeriodType.getId().toString());
    }
  }
}
