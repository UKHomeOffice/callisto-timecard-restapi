package uk.gov.homeoffice.digital.sas.timecard.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
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
    return String.valueOf(timeEntry.getTenantId() + ":" + timeEntry.getOwnerId());
  }

  @Autowired
  public void setProducerService(KafkaProducerService<TimeEntry> kafkaProducerService) {
    super.createProducerService(kafkaProducerService);
  }

  @PrePersist
  void sendMessageOnCreate(TimeEntry resource) {
    super.sendKafkaMessageOnCreate(resource, String.valueOf(resource.getOwnerId()));
  }

  @PreUpdate
  void sendMessageOnUpdate(TimeEntry resource) {
    super.sendKafkaMessageOnUpdate(resource, String.valueOf(resource.getOwnerId()));
  }

  @PreRemove
  void sendMessageOnDelete(TimeEntry resource) {
    super.sendKafkaMessageOnDelete(resource, String.valueOf(resource.getOwnerId()));
  }

}
