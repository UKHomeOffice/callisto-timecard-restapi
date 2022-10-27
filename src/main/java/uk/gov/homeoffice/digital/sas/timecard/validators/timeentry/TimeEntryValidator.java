package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.json.simple.JSONObject;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;
import uk.gov.homeoffice.digital.sas.timecard.utils.BeanUtil;

import java.sql.Time;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TimeEntryValidator implements ConstraintValidator<TimeEntryConstraint, Object> {


  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
    Session session = entityManager.unwrap(Session.class);
    /* We need to manually control the session here as auto flushing after the db read
    will cause an infinite loop of entity validation
     */
    session.setHibernateFlushMode(FlushMode.MANUAL);

    var timeEntry = (TimeEntry) value;
    var timeEntryClashes = BeanUtil.getBean(TimeEntryRepository.class).findAllClashingTimeEntries(
        timeEntry.getOwnerId() != null ? timeEntry.getOwnerId().toString() : null,
        timeEntry.getId() != null ? timeEntry.getId().toString() : null,
        timeEntry.getActualStartTime(),
        timeEntry.getActualEndTime());

    session.setHibernateFlushMode(FlushMode.AUTO);
    if (!timeEntryClashes.isEmpty()) {
      var payload = timeEntryClashes.stream().map(this::transformTimeEntry)
          .collect(Collectors.toCollection(ArrayList::new));

      HibernateConstraintValidatorContext hibernateContext =
          context.unwrap(HibernateConstraintValidatorContext.class);

      hibernateContext.disableDefaultConstraintViolation();
      hibernateContext.withDynamicPayload(payload);
      hibernateContext
          .buildConstraintViolationWithTemplate(
              "Time periods must not overlap with another time period")
          .addPropertyNode("actualStartTime")
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private JSONObject transformTimeEntry(TimeEntry timeEntry) {
    var result = new JSONObject();
    result.put("startTime", timeEntry.getActualStartTime());
    result.put("endTime", timeEntry.getActualEndTime());
    result.put("timePeriodTypeId", timeEntry.getTimePeriodTypeId());
    return result;
  }
}