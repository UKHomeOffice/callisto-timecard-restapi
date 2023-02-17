package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import uk.gov.homeoffice.digital.sas.timecard.enums.ErrorMessage;
import uk.gov.homeoffice.digital.sas.timecard.enums.InvalidField;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;
import uk.gov.homeoffice.digital.sas.timecard.utils.BeanUtil;

public class TimeEntryValidator implements ConstraintValidator<TimeEntryConstraint, TimeEntry> {

  @Override
  public boolean isValid(TimeEntry timeEntry, ConstraintValidatorContext context) {

    if (timeEntry.getActualEndTime() != null
        && timeEntry.getActualStartTime().after(timeEntry.getActualEndTime())) {
      var message = ErrorMessage.END_TIME_BEFORE_START_TIME.toString();
      var invalidField = InvalidField.END_TIME;

      addConstraintViolationToContext(context, message, invalidField, null);
      return false;
    }

    var timeEntryClashes = getClashingTimeEntries(timeEntry);
    if (!timeEntryClashes.isEmpty()) {
      var invalidField = getClashingField(timeEntry, timeEntryClashes);
      var payload = timeEntryClashes.stream().map(this::transformTimeEntry)
          .collect(Collectors.toCollection(ArrayList::new));
      var message = ErrorMessage.TIME_PERIOD_CLASH.toString();

      addConstraintViolationToContext(context, message, invalidField, payload);
      return false;
    }
    return true;
  }

  private void addConstraintViolationToContext(ConstraintValidatorContext context,
                                                      String message,
                                                      InvalidField clashingProperty,
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

  private List<TimeEntry> getClashingTimeEntries(TimeEntry timeEntry) {
    var entityManager = BeanUtil.getBean(EntityManager.class);
    var session = entityManager.unwrap(Session.class);
    /* We need to manually control the session here as auto flushing after the db read
    will cause an infinite loop of entity validation
     */
    session.setHibernateFlushMode(FlushMode.MANUAL);

    var timeEntryClashes = BeanUtil.getBean(TimeEntryRepository.class).findAllClashingTimeEntries(
        timeEntry.getOwnerId() != null ? timeEntry.getOwnerId() : null,
        timeEntry.getId() != null ? timeEntry.getId() : null,
        timeEntry.getTenantId() != null ? timeEntry.getTenantId() : null,
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

  private InvalidField getClashingField(
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
      return InvalidField.START_AND_END_TIME;
    }
    if (startTimeClash) {
      return InvalidField.START_TIME;
    }
    if (endTimeClash) {
      return InvalidField.END_TIME;
    }

    return InvalidField.START_AND_END_TIME;
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