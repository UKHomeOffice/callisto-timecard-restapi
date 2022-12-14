package uk.gov.homeoffice.digital.sas.timecard.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
public class TimeEntryKafkaEntityListener extends KafkaEntityListener<TimeEntry> {

  @Override
  public String resolveMessageKey(TimeEntry timeEntry) {
    return timeEntry.getOwnerId().toString();
  }

  @Autowired
  public void createProducerService(KafkaProducerService<TimeEntry> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }
}
