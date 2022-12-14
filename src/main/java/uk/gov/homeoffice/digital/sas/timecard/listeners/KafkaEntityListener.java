package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Component
public abstract class KafkaEntityListener<T> {

  private KafkaProducerService<T> kafkaProducerService;

  public abstract String resolveMessageKey(T resource);

  @Autowired
  public void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  @PostPersist
  protected void sendKafkaMessageOnCreate(T resource) {
    kafkaProducerService.sendMessage(resolveMessageKey(resource),
        (Class<T>) resource.getClass(), resource, KafkaAction.CREATE);
  }

  @PostUpdate
  protected void sendKafkaMessageOnUpdate(T resource) {
    kafkaProducerService.sendMessage(resolveMessageKey(resource),
        (Class<T>) resource.getClass(), resource, KafkaAction.UPDATE);
  }
}
