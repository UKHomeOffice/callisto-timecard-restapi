package uk.gov.homeoffice.digital.sas.timecard.listeners;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
public class TimeEntryKafkaEntityListener extends KafkaEntityListener<TimeEntry> {

  private Environment environment;

  @Override
  public String resolveMessageKey(TimeEntry timeEntry) {
    return timeEntry.getOwnerId().toString();
  }

  @Autowired
  public void setProducerService(KafkaProducerService<TimeEntry> kafkaProducerService,
                                 Environment environment) {
    super.createProducerService(kafkaProducerService);
    this.environment = environment;
  }

  @PostPersist
  void sendMessageOnCreate(TimeEntry resource) {
    if (isLocalHost()) {
      super.sendKafkaMessageOnCreate(resource);
    }
  }

  @PostUpdate
  void sendMessageOnUpdate(TimeEntry resource) {
    if (isLocalHost()) {
      super.sendKafkaMessageOnUpdate(resource);
    }
  }

  @PostRemove
  void sendMessageOnDelete(TimeEntry resource) {
    if (isLocalHost()) {
      super.sendKafkaMessageOnDelete(resource);
    }
  }


  // temporary check to prevent Kafka in dev environment
  private boolean isLocalHost() {
    return Arrays.stream(this.environment.getActiveProfiles()).toList()
        .contains("localhost");
  }
}
