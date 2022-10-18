package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import uk.gov.homeoffice.digital.sas.timecard.BeanUtil;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeEntryValidator implements ConstraintValidator<TimeEntryConstraint, Object> {


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        Session session = entityManager.unwrap(Session.class);
        //we need to manually control the session here as auto flushing after the db read will cause an infinite loop of entity validation
        session.setHibernateFlushMode(FlushMode.MANUAL);

        var timeEntry = (TimeEntry) value;
        var timeEntryClashes= BeanUtil.getBean(TimeEntryRepository.class).findAllClashingTimeEntries(
                timeEntry.getOwnerId(),
                timeEntry.getId() != null ? timeEntry.getId().toString() : null,
                timeEntry.getActualStartTime(),
                timeEntry.getActualEndTime());

        session.setHibernateFlushMode(FlushMode.AUTO);
        return timeEntryClashes.isEmpty();
    }
}