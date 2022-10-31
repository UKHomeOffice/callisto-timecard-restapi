package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
      ClashingProperty clashingProperty = getClashingProperty(timeEntry, timeEntryClashes);
      var payload = timeEntryClashes.stream().map(this::transformTimeEntry)
          .collect(Collectors.toCollection(ArrayList::new));

      HibernateConstraintValidatorContext hibernateContext =
          context.unwrap(HibernateConstraintValidatorContext.class);

      hibernateContext.disableDefaultConstraintViolation();
      hibernateContext.withDynamicPayload(payload);
      hibernateContext
          .buildConstraintViolationWithTemplate(
              "Time periods must not overlap with another time period")
          .addPropertyNode(clashingProperty.toString())
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

  private ClashingProperty getClashingProperty(TimeEntry timeEntry, List<TimeEntry> timeEntryClashes) {
    var startTimeClash = false;
    var endTimeClash = false;

    if (timeEntry.getActualStartTime().equals(timeEntryClashes.get(0).getActualStartTime())) {
      startTimeClash = true;
    }

    if (timeEntryClashes.get(0).getActualEndTime() != null && timeEntryClashes.get(0).getActualStartTime().compareTo(timeEntry.getActualStartTime()) <= 0 && timeEntry.getActualStartTime().compareTo(timeEntryClashes.get(0).getActualEndTime()) < 0) {
      startTimeClash = true;
    }

    if (timeEntry.getActualEndTime() != null && timeEntry.getActualStartTime().compareTo(timeEntryClashes.get(0).getActualStartTime()) <= 0 && timeEntryClashes.get(0).getActualStartTime().compareTo(timeEntry.getActualEndTime() != null ? timeEntry.getActualEndTime() : null) < 0) {
      endTimeClash = true;
    }

    if (startTimeClash && endTimeClash) return ClashingProperty.startAndEndTime;
    if (startTimeClash) return ClashingProperty.startTime;
    if (endTimeClash) return ClashingProperty.endTime;

    return null;
  }

  enum ClashingProperty {
      startTime, endTime, startAndEndTime
  }
}