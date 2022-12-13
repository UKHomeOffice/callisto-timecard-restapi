package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Component
public class KafkaEntityListener<T extends BaseEntity> {

  private KafkaProducerService<T> kafkaProducerService;

  @Autowired
  public void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  @PostPersist
  private void sendKafkaMessageOnCreate(T resource) {
    kafkaProducerService.sendMessage((Class<T>) resource.getClass(), resource, KafkaAction.CREATE);
  }

  @PostUpdate
  private void sendKafkaMessageOnUpdate(T resource) {
    kafkaProducerService.sendMessage((Class<T>) resource.getClass(), resource, KafkaAction.UPDATE);
  }
}
