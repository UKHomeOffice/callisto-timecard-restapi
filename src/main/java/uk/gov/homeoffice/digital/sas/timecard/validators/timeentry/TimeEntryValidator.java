package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.homeoffice.digital.sas.jparest.exceptions.ResourceConstraintViolationException;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class TimeEntryValidator {

    private final TimeEntryRepository timeEntryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TimeEntryValidator(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }


    public void validate(TimeEntry timeEntry) {
        Session session = entityManager.unwrap(Session.class);
        session.setHibernateFlushMode(FlushMode.MANUAL);
        var timeEntryId = timeEntry.getId() != null ? timeEntry.getId().toString() : null;
        if (!timeEntryRepository.findAllClashingTimeEntries(
                timeEntry.getOwnerId(),
                timeEntryId,
                timeEntry.getActualStartTime(),
                timeEntry.getActualEndTime()).isEmpty()) {
            throw new ResourceConstraintViolationException(
                    "Cannot have clashes");
        }
        session.setHibernateFlushMode(FlushMode.AUTO);
    }

}

