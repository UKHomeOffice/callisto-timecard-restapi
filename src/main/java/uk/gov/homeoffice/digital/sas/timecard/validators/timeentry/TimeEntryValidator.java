package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.homeoffice.digital.sas.jparest.exceptions.ResourceConstraintViolationException;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

@Service
public class TimeEntryValidator {

    private final TimeEntryRepository timeEntryRepository;

    @Autowired
    public TimeEntryValidator(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }


    public void validate(TimeEntry timeEntry) {

        if (!timeEntryRepository.findAllClashingTimeEntries(
                timeEntry.getOwnerId(),
                timeEntry.getActualStartTime()).isEmpty()) {
            throw new ResourceConstraintViolationException(
                    "Cannot have clashes");
        }


    }

}

