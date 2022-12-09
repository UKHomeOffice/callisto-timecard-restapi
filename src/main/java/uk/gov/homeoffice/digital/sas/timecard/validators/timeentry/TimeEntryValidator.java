package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import uk.gov.homeoffice.digital.sas.timecard.enums.ClashingProperty;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;
import uk.gov.homeoffice.digital.sas.timecard.utils.BeanUtil;

public class TimeEntryValidator implements ConstraintValidator<TimeEntryConstraint, TimeEntry> {

  @Override
  public boolean isValid(TimeEntry timeEntry, ConstraintValidatorContext context) {

    if (timeEntry.getActualEndTime() != null
        && timeEntry.getActualStartTime().after(timeEntry.getActualEndTime())) {
      String message = "End time must be after start time";
      ClashingProperty clashingProperty = ClashingProperty.END_TIME;

      addConstraintViolationToContext(context, message, clashingProperty, null);
      return false;
    }

    List<TimeEntry> timeEntryClashes = getClashingTimeEntries(timeEntry);
    if (!timeEntryClashes.isEmpty()) {
      ClashingProperty clashingProperty = getClashingProperty(timeEntry, timeEntryClashes);
      var payload = timeEntryClashes.stream().map(this::transformTimeEntry)
          .collect(Collectors.toCollection(ArrayList::new));
      String message = "Time periods must not overlap with another time period";

      addConstraintViolationToContext(context, message, clashingProperty, payload);
      return false;
    }
    return true;
  }

  private static void addConstraintViolationToContext(ConstraintValidatorContext context,
                                                      String message,
                                                      ClashingProperty clashingProperty,
                                                      ArrayList<TimeClash> payload) {
    HibernateConstraintValidatorContext hibernateContext =
        context.unwrap(HibernateConstraintValidatorContext.class);

    hibernateContext.disableDefaultConstraintViolation();
    hibernateContext.withDynamicPayload(payload);
    hibernateContext
        .buildConstraintViolationWithTemplate(
            message)
        .addPropertyNode(clashingProperty.toString())
        .addConstraintViolation();
  }

  private static List<TimeEntry> getClashingTimeEntries(TimeEntry timeEntry) {
    EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
    Session session = entityManager.unwrap(Session.class);
    /* We need to manually control the session here as auto flushing after the db read
    will cause an infinite loop of entity validation
     */
    session.setHibernateFlushMode(FlushMode.MANUAL);

    var timeEntryClashes = BeanUtil.getBean(TimeEntryRepository.class).findAllClashingTimeEntries(
        timeEntry.getOwnerId() != null ? timeEntry.getOwnerId().toString() : null,
        timeEntry.getId() != null ? timeEntry.getId().toString() : null,
        timeEntry.getTenantId() != null ? timeEntry.getTenantId().toString() : null,
        timeEntry.getActualStartTime(),
        timeEntry.getActualEndTime());

    session.setHibernateFlushMode(FlushMode.AUTO);
    return timeEntryClashes;
  }

  private TimeClash transformTimeEntry(TimeEntry timeEntry) {
    return new TimeClash(timeEntry.getActualStartTime(),
        timeEntry.getActualEndTime(),
        timeEntry.getTimePeriodTypeId());
  }

  private ClashingProperty getClashingProperty(
      TimeEntry timeEntry, List<TimeEntry> timeEntryClashes) {
    var startTimeClash = false;
    var endTimeClash = false;

    for (TimeEntry timeEntryClash : timeEntryClashes) {
      if (startTimeClashes(timeEntry, timeEntryClash)) {
        startTimeClash = true;
      }

      if (endTimeClashes(timeEntry, timeEntryClash)) {
        endTimeClash = true;
      }

      if (startTimeClash && endTimeClash) {
        break;
      }
    }

    if (startTimeClash && endTimeClash) {
      return ClashingProperty.START_AND_END_TIME;
    }
    if (startTimeClash) {
      return ClashingProperty.START_TIME;
    }
    if (endTimeClash) {
      return ClashingProperty.END_TIME;
    }

    return ClashingProperty.START_AND_END_TIME;
  }

  private boolean startTimeClashes(TimeEntry timeEntry, TimeEntry timeEntryClash) {
    return timeEntry.getActualStartTime().equals(timeEntryClash.getActualStartTime())
        || timeEntryClash.getActualEndTime() != null
        && startTimeClashesWithTimeEntry(timeEntry.getActualStartTime(), timeEntryClash);
  }

  private boolean endTimeClashes(TimeEntry timeEntry, TimeEntry timeEntryClash) {
    return timeEntry.getActualEndTime() != null
        && timeEntryClash.getActualEndTime() != null
        && (endTimeClashesWithTimeEntry(timeEntry.getActualEndTime(), timeEntryClash)
        || startTimeClashesWithTimeEntry(timeEntryClash.getActualStartTime(), timeEntry));
  }

  private boolean endTimeClashesWithTimeEntry(Date endTime, TimeEntry timeEntry) {
    return (endTime.after(timeEntry.getActualStartTime())
        && endTime.getTime() <= timeEntry.getActualEndTime().getTime());
  }

  private boolean startTimeClashesWithTimeEntry(Date startTime, TimeEntry timeEntry) {
    return (startTime.getTime() >= timeEntry.getActualStartTime().getTime()
        && startTime.before(timeEntry.getActualEndTime()));
  }
}