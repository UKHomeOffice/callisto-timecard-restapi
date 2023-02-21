package uk.gov.homeoffice.digital.sas.timecard.listeners;

import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaDbTransactionSynchronizer;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Slf4j
public abstract class KafkaEntityListener<T> {

  protected KafkaProducerService<T> kafkaProducerService;
  @Autowired
  private KafkaDbTransactionSynchronizer kafkaDbTransactionSynchronizer;

  protected abstract String resolveMessageKey(T resource);

  protected void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  protected void sendKafkaMessageOnCreate(T resource, String id) {
    sendMessage(resource, KafkaAction.CREATE, id);
  }

  protected void sendKafkaMessageOnUpdate(T resource, String id) {
    sendMessage(resource, KafkaAction.UPDATE, id);
  }

  protected void sendKafkaMessageOnDelete(T resource, String id) {
    sendMessage(resource, KafkaAction.DELETE, id);
  }

  @SuppressWarnings("unchecked")
  private void sendMessage(T resource, KafkaAction action, String id) {
    BiConsumer<KafkaAction, String> sendMessageConsumer =
        (KafkaAction actionArg, String messageKeyArg) -> kafkaProducerService.sendMessage(
              messageKeyArg, (Class<T>) resource.getClass(), resource, actionArg);

    kafkaDbTransactionSynchronizer.registerSynchronization(
        action, resolveMessageKey(resource), id, sendMessageConsumer);

  }
}
