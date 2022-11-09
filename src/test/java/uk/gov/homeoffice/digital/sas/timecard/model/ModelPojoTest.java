package uk.gov.homeoffice.digital.sas.timecard.model;

import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

class ModelPojoTest {
    @Test
    void validate() {
        Validator validator = ValidatorBuilder.create()
                .with(new SetterTester(),
                        new GetterTester())
                .build();
        validator.validate(this.getClass().getPackageName());
    }
}