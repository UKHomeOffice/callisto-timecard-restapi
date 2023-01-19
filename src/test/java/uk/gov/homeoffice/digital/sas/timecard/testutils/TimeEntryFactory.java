package uk.gov.homeoffice.digital.sas.timecard.testutils;

import java.util.Date;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

public final class TimeEntryFactory {

  private TimeEntryFactory() {}

  public static TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime) {
    var timeEntry = new TimeEntry();
    timeEntry.setOwnerId(ownerId);
    timeEntry.setActualStartTime(actualStartTime);
    return timeEntry;
  }

  public static TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime, Date actualEndTime) {
    var timeEntry = createTimeEntry(ownerId, actualStartTime);
    timeEntry.setActualEndTime(actualEndTime);
    return timeEntry;
  }
}
