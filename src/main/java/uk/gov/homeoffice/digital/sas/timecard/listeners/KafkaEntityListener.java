package uk.gov.homeoffice.digital.sas.timecard.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Slf4j
public abstract class KafkaEntityListener<T> {

  protected KafkaProducerService<T> kafkaProducerService;

  protected abstract String resolveMessageKey(T resource);

  protected void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  protected void sendKafkaMessageOnCreate(T resource, String ownerId) {
    sendMessage(resource, KafkaAction.CREATE, ownerId);
  }

  protected void sendKafkaMessageOnUpdate(T resource, String ownerId) {
    sendMessage(resource, KafkaAction.UPDATE, ownerId);
  }

  protected void sendKafkaMessageOnDelete(T resource, String ownerId) {
    sendMessage(resource, KafkaAction.DELETE, ownerId);
  }

  @SuppressWarnings("unchecked")
  private void sendMessage(T resource, KafkaAction action, String ownerId) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          int status = TransactionSynchronization.STATUS_UNKNOWN;
          String messageKey = resolveMessageKey(resource);

          @Override
          public void beforeCommit(boolean readOnly) {
            log.info(String.format("Kafka Transaction [ %s ] Initialized with message key [ %s ]",
                action, messageKey));
            kafkaProducerService.sendMessage(messageKey,
                (Class<T>) resource.getClass(), resource, action);
          }

          @Override
          public void afterCommit() {
            log.info(String.format(
                "Database transaction [ %s ] with ownerId [ %s ] was successful",
                action.toString(), ownerId));
            status = TransactionSynchronization.STATUS_COMMITTED;
          }

          @Override
          public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED) {
              log.info(String.format(
                  "Transaction successful with messageKey [ %s ]", messageKey));

            } else {
              log.error(String.format(
                  "Database transaction [ %s ] with ownerId [ %s ] failed", action, ownerId));
            }
          }
        }
    );
  }
}
