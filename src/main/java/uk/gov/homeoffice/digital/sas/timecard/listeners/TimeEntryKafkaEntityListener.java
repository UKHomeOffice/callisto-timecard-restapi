package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
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
  public void setProducerService(KafkaProducerService<TimeEntry> kafkaProducerService) {
    super.createProducerService(kafkaProducerService);
  }

  @PostPersist
  private void sendMessageOnCreate(TimeEntry  resource) {
    super.sendKafkaMessageOnCreate(resource);
  }

  @PostUpdate
  private void sendMessageOnUpdate(TimeEntry resource) {
    super.sendKafkaMessageOnUpdate(resource);
  }
}
