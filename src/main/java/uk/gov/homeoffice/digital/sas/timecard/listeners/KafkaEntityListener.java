package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

public abstract class KafkaEntityListener<T> {

  protected KafkaProducerService<T> kafkaProducerService;

  public abstract String resolveMessageKey(T resource);

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
