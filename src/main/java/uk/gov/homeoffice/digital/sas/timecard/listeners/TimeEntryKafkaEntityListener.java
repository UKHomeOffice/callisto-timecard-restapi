package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
public class TimeEntryKafkaEntityListener extends KafkaEntityListener<TimeEntry> {

  @Value("${spring.kafka.template.default-topic}")
  private String topicName;

  @Override
  public String resolveMessageKey(TimeEntry timeEntry) {
    return timeEntry.getTenantId().toString() + ":" + timeEntry.getOwnerId().toString();
  }

  @Autowired
  public void setProducerService(KafkaProducerService<TimeEntry> kafkaProducerService) {
    super.createProducerService(kafkaProducerService);
  }

  @PrePersist
  void sendMessageOnCreate(TimeEntry resource) {
    super.sendKafkaMessageOnCreate(resource, resource.getOwnerId().toString());
  }

  @PreUpdate
  void sendMessageOnUpdate(TimeEntry resource) {
    super.sendKafkaMessageOnUpdate(resource, resource.getOwnerId().toString());
  }

  @PreRemove
  void sendMessageOnDelete(TimeEntry resource) {
    super.sendKafkaMessageOnDelete(resource, resource.getOwnerId().toString());
  }

}
