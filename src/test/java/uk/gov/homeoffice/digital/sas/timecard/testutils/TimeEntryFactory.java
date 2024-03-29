package uk.gov.homeoffice.digital.sas.timecard.testutils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;

public final class TimeEntryFactory {

  private TimeEntryFactory() {}

  public static TimeEntry createTimeEntry() {
    return createTimeEntry(UUID.randomUUID(), UUID.randomUUID(), getAsDate(LocalDateTime.now()));
  }

  public static TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime) {
    return createTimeEntry(ownerId, UUID.randomUUID(), actualStartTime, null);
  }

  public static TimeEntry createTimeEntry(UUID ownerId, Date actualStartTime, Date actualEndTime) {
    return createTimeEntry(ownerId, UUID.randomUUID(), actualStartTime, actualEndTime);
  }

  public static TimeEntry createTimeEntry(UUID ownerId, UUID tenantId, Date actualStartTime) {
    return createTimeEntry(ownerId, tenantId, actualStartTime, null);
  }

  public static TimeEntry createTimeEntry(UUID ownerId, UUID tenantId, Date actualStartTime,
                                          Date actualEndTime) {
    var timeEntry = new TimeEntry();
    timeEntry.setOwnerId(ownerId);
    timeEntry.setTenantId(tenantId);
    timeEntry.setActualStartTime(actualStartTime);
    timeEntry.setActualEndTime(actualEndTime);
    return timeEntry;
  }
}
