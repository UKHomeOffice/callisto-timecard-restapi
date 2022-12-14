package uk.gov.homeoffice.digital.sas.timecard.listeners;

import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

public class TimeEntryKafkaEntityListener extends KafkaEntityListener<TimeEntry> {

  @Override
  public String resolveMessageKey(TimeEntry timeEntry) {
    return timeEntry.getOwnerId().toString();
  }
}
