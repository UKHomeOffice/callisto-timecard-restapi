package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TimeEntryValidator.class)
public @interface TimeEntryConstraint {
  String message() default "Time entry validation error";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}