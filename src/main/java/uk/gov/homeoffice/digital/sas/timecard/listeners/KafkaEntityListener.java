package uk.gov.homeoffice.digital.sas.timecard.listeners;

import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

public abstract class KafkaEntityListener<T> {

  protected KafkaProducerService<T> kafkaProducerService;

  public abstract String resolveMessageKey(T resource);

  protected void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  protected void sendKafkaMessageOnCreate(T resource) {
    sendMessage(resource, KafkaAction.CREATE);
  }

  protected void sendKafkaMessageOnUpdate(T resource) {
    sendMessage(resource, KafkaAction.UPDATE);
  }

  private void sendMessage(T resource, KafkaAction action) {
    kafkaProducerService.sendMessage(resolveMessageKey(resource),
        (Class<T>) resource.getClass(), resource, action);
  }
}
