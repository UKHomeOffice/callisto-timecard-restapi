package uk.gov.homeoffice.digital.sas.timecard.listeners;

import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

public class TimeEntryKafkaEntityListener extends KafkaEntityListener {

  @Override
  public String resolveMessageKey(Object resource) throws Exception {
    if (resource instanceof TimeEntry timeEntry) {
      return timeEntry.getOwnerId().toString();
    } else {
      throw new Exception("Expected resource to be of type TimeEntry but was of type ["
          + resource.getClass() + "]");
    }
  }
}
