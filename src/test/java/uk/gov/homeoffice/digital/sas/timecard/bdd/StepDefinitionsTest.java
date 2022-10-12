package uk.gov.homeoffice.digital.sas.timecard.bdd;

import io.cucumber.java.BeforeAll;
import uk.gov.homeoffice.digital.sas.cucumberjparest.JpaTestContext;

public class StepDefinitionsTest {

    @BeforeAll
    public static void before_all() {
        JpaTestContext.serviceRegistry.addService("timecard", "http://localhost:9090");
    }
}
