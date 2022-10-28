package uk.gov.homeoffice.digital.sas.timecard.validators.timeentry;


import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.repositories.TimeEntryRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@Transactional
public class TimeEntryValidatorTest {

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final static UUID OWNER_ID_1 = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    private final static LocalDateTime EXISTING_SHIFT_START_TIME = LocalDateTime.of(
            2022, 1, 1, 9, 0, 0);
    private final static LocalDateTime EXISTING_SHIFT_END_TIME = LocalDateTime.of(
            2022, 1, 1, 17, 0, 0);

    @BeforeEach
    void saveTimeEntry() {
        saveEntryAndFlushDatabase(createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME),
                getAsDate(EXISTING_SHIFT_END_TIME)));
    }

    // region clashing_error_tests

    // existing: 08:00-, new: 08:00-
    @Test
    void validate_newStartTimeIsTheSameAsExistingStartTimeWithNoEndTimes_errorReturned() {
        var time = LocalDateTime.of(
                2022, 1, 1, 8, 0, 0);

        saveEntryAndFlushDatabase(createTimeEntry(
                OWNER_ID_1,
                getAsDate(time)));

        var newStartTime = getAsDate(time);

        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 09:00-
    @Test
    void validate_newStartTimeIsTheSameAsExistingStartTimeAndNoNewEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME);

        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 09:01-
    @Test
    void validate_newStartTimeInBetweenExistingStartAndEndTimeAndNoNewEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME.plusMinutes(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 08:59-16:59
    @Test
    void validate_existingStartTimeInBetweenNewStartAndEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME.minusMinutes(1));
        var newEndTime = getAsDate(EXISTING_SHIFT_END_TIME.minusMinutes(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime, newEndTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 16:59-17:01
    @Test
    void validate_newStartTimeBeforeExistingEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_END_TIME.minusMinutes(1));
        var newEndTime = getAsDate(EXISTING_SHIFT_END_TIME.plusMinutes(1));
        var newTimeEntry = createTimeEntry(OWNER_ID_1, newStartTime, newEndTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(newTimeEntry));
    }

    // existing: 09:00-17:00, new: 09:00-17:00
    @Test
    void validate_newStartAndEndTimeSameAsExistingStartAndEndTime_errorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME);
        var newEndTime = getAsDate(EXISTING_SHIFT_END_TIME);
        var newTimeEntry = createTimeEntry(OWNER_ID_1, newStartTime, newEndTime);

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(newTimeEntry));
    }

    // existing: 07:00-08:00, updated: 06:00-08:00
    @Test
    void validate_timeEntryIdsAreDifferentAndTimesClash_errorReturned() {

        var existingTimeEntry = createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(2)),
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1)));

        saveEntryAndFlushDatabase(existingTimeEntry);

        var newTimeEntry = createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(2)),
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1)));

        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                saveEntryAndFlushDatabase(newTimeEntry));
    }

    // endregion

    // region happy_path

    // existing: 09:00-17:00, new: 17:01-
    @Test
    void validate_newStartTimeAfterExistingEndTimeAndNoEndTime_noErrorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_END_TIME.plusMinutes(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 17:00-
    @Test
    void validate_newStartTimeEqualToExistingEndTimeAndNoEndTime_noErrorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_END_TIME);
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 08:00-09:00
    @Test
    void validate_newEndTimeEqualToExistingStartTime_noErrorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1));
        var newEndTime = getAsDate(EXISTING_SHIFT_START_TIME);
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime, newEndTime);

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 08:59-
    @Test
    void validate_newStartTimeBeforeExistingStartTimeAndNoEndTime_noErrorReturned() {
        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME.minusMinutes(1));
        var timeEntryNew = createTimeEntry(OWNER_ID_1, newStartTime);

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 09:00-17:00, new: 09:00-
    @Test
    void validate_clashingTimeEntryForDifferentOwner_noErrorReturned() {
        var newOwnerId = UUID.randomUUID();

        var newStartTime = getAsDate(EXISTING_SHIFT_START_TIME);

        var timeEntryNew = createTimeEntry(newOwnerId, newStartTime);

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(timeEntryNew));
    }

    // existing: 07:00-08:00, updated: 05:00-06:00
    @Test
    void validate_timeEntryIdsAreTheSameAndNoClash_noErrorReturned() {

        var newTimeEntry = createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(2)),
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1)));

        saveEntryAndFlushDatabase(newTimeEntry);

        newTimeEntry.setActualStartTime(getAsDate(EXISTING_SHIFT_START_TIME.minusHours(4)));
        newTimeEntry.setActualEndTime(getAsDate(EXISTING_SHIFT_START_TIME.minusHours(3)));

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(newTimeEntry));
    }

    // existing: 07:00-08:00, updated: 06:00-08:00
    @Test
    void validate_timeEntryIdsAreTheSameAndTimesClash_noErrorReturned() {

        var newTimeEntry = createTimeEntry(
                OWNER_ID_1,
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(2)),
                getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1)));

        saveEntryAndFlushDatabase(newTimeEntry);

        newTimeEntry.setActualStartTime(getAsDate(EXISTING_SHIFT_START_TIME.minusHours(3)));
        newTimeEntry.setActualEndTime(getAsDate(EXISTING_SHIFT_START_TIME.minusHours(1)));

        assertThatNoException().isThrownBy(() ->
                saveEntryAndFlushDatabase(newTimeEntry));
    }

    // endregion

    private Date getAsDate(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    private TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime) {
        var timeEntry = new TimeEntry();
        timeEntry.setOwnerId(ownerId);
        timeEntry.setActualStartTime(actualStartTime);
        return timeEntry;
    }
    private TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime, Date actualEndTime) {
        var timeEntry = createTimeEntry(ownerId, actualStartTime);
        timeEntry.setActualEndTime(actualEndTime);
        return timeEntry;
    }

    private void saveEntryAndFlushDatabase(TimeEntry existingTimeEntry) {
        Session session = entityManager.unwrap(Session.class);
        session.setHibernateFlushMode(FlushMode.MANUAL);

        timeEntryRepository.save(existingTimeEntry);

        session.flush();
        session.setHibernateFlushMode(FlushMode.AUTO);
    }

}