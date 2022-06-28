package uk.gov.homeoffice.digital.sas.timecard;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:features/"},
        plugin = {"pretty","html:target/cucumber"}
)
public class CucumberRunnerTest {
}

