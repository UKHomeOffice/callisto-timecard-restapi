package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.homeoffice.digital.sas.jparest.exceptions.ResourceConstraintViolationException;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    private final static Integer OWNER_ID_1 = 1;
    private final static LocalDateTime EXISTING_SHIFT_START_TIME = LocalDateTime.of(
            2022, 1, 1, 9, 0, 0);
    private final static LocalDateTime EXISTING_SHIFT_END_TIME = LocalDateTime.of(
            2022, 1, 1, 17, 0, 0);


    @BeforeEach
    void saveTimeEntry() {
        timeEntryRepository.save(createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME),
                getAsDate(EXISTING_SHIFT_END_TIME)));
    }



    @Test
    void validate_startTimeIsTheSame_errorReturned() {

        var timeEntryNew = createTimeEntry(OWNER_ID_1, getAsDate(EXISTING_SHIFT_START_TIME));

        assertThatExceptionOfType(ResourceConstraintViolationException.class).isThrownBy(() ->
                timeEntryValidator.validate(timeEntryNew));
    }

    @Test
    void validate_newStartTimeAfterExistingEndTimeAndNoEndTime_noErrorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_END_TIME.plusDays(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatNoException().isThrownBy(() ->
                timeEntryValidator.validate(timeEntryNew));
    }

    @Test
    void validate_newStartTimeInBetweenExistingStartAndEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME.plusHours(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatExceptionOfType(ResourceConstraintViolationException.class).isThrownBy(() ->
                timeEntryValidator.validate(timeEntryNew));
    }

    private Date getAsDate(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    private TimeEntry createTimeEntry(Integer ownerId, Date actualStartTime) {
        var timeEntry = new TimeEntry();
        timeEntry.setOwnerId(ownerId);
        timeEntry.setActualStartTime(actualStartTime);
        return timeEntry;
    }
    private TimeEntry createTimeEntry(Integer ownerId, Date actualStartTime, Date actualEndTime) {
        var timeEntry = createTimeEntry(ownerId, actualStartTime);
        timeEntry.setActualEndTime(actualEndTime);
        return timeEntry;
    }



}
