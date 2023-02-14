package uk.gov.homeoffice.digital.sas.timecard.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Slf4j
public abstract class KafkaEntityListener<T> {

  protected KafkaProducerService<T> kafkaProducerService;

  public abstract String resolveMessageKey(T resource);

  protected void createProducerService(KafkaProducerService<T> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  protected void sendKafkaMessageOnCreate(T resource, String topicName) {
      sendMessage(resource, KafkaAction.CREATE, topicName);
  }

  protected void sendKafkaMessageOnUpdate(T resource, String topicName) {
    sendMessage(resource, KafkaAction.UPDATE, topicName);
  }

  protected void sendKafkaMessageOnDelete(T resource, String topicName) {
    sendMessage(resource, KafkaAction.DELETE, topicName);
  }

  @SuppressWarnings("unchecked")
  private void sendMessage(T resource, KafkaAction action, String topicName) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          int status = TransactionSynchronization.STATUS_UNKNOWN;
          String messageKey = resolveMessageKey(resource);

          @Override
          public void beforeCommit(boolean readOnly) {
            kafkaProducerService.sendMessage(messageKey,
                (Class<T>) resource.getClass(), resource, action);
          }

          @Override
          public void afterCommit(){
            status = TransactionSynchronization.STATUS_COMMITTED;
          }

          @Override
          public void afterCompletion(int status){
            if (status == STATUS_COMMITTED) {
              log.info(String.format("Database transaction successful with messageKey [ %s ] sent" +
                      " to " +
                  "topic [ %s ], with action [ %s ]", messageKey, topicName,
                  action.toString()));

            } else {
              log.error(String.format("Database transaction failed with messageKey [%s] sent to " +
                  "topic " +
                  "[%s], with action [%s]", messageKey, topicName, action.toString()));
            }
          }
        }
    );
  }
}
