package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.homeoffice.digital.sas.jparest.exceptions.ResourceConstraintViolationException;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@Transactional
public class TimeEntryValidatorTest {


    @Autowired
    private TimeEntryValidator timeEntryValidator;

    @Autowired
    private TimeEntryRepository timeEntryRepository;




    @Test
    void validate_startTimeIsTheSame_errorReturned() {

        var today = Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay().toInstant(ZoneOffset.UTC));

        var timeEntry = new TimeEntry();
        timeEntry.setOwnerId(1);
        timeEntry.setActualStartTime(today);
        timeEntryRepository.save(timeEntry);

        var timeEntryNew = new TimeEntry();
        timeEntryNew.setOwnerId(1);
        timeEntryNew.setActualStartTime(today);


        assertThatExceptionOfType(ResourceConstraintViolationException.class).isThrownBy(() ->
                timeEntryValidator.validate(timeEntryNew));

    }

    @Test
    void validate_startTimeIsDifferent_noErrorReturned() {


        var today = LocalDate.of(2020, 1, 1);
        var tomorrow = today.plusDays(1L);

        var timeEntry = new TimeEntry();
        timeEntry.setOwnerId(1);
        timeEntry.setActualStartTime(Date.from(today.atStartOfDay().toInstant(ZoneOffset.UTC)));
        timeEntryRepository.save(timeEntry);

        var timeEntryNew = new TimeEntry();
        timeEntryNew.setOwnerId(1);
        timeEntryNew.setActualStartTime(Date.from(tomorrow.atStartOfDay().toInstant(ZoneOffset.UTC)));


        assertThatNoException().isThrownBy(() ->
                timeEntryValidator.validate(timeEntryNew));

    }



}
