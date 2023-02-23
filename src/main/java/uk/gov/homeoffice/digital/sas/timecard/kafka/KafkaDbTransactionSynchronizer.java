package uk.gov.homeoffice.digital.sas.timecard.kafka;

import static uk.gov.homeoffice.digital.sas.timecard.kafka.constants.Constants.DATABASE_TRANSACTION_FAILED;
import static uk.gov.homeoffice.digital.sas.timecard.kafka.constants.Constants.DATABASE_TRANSACTION_SUCCESSFUL;
import static uk.gov.homeoffice.digital.sas.timecard.kafka.constants.Constants.KAFKA_TRANSACTION_INITIALIZED;
import static uk.gov.homeoffice.digital.sas.timecard.kafka.constants.Constants.TRANSACTION_SUCCESSFUL;
import static uk.gov.homeoffice.digital.sas.timecard.kafka.constants.Constants.WITH_ENTITY_ID;

import java.util.function.BiConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;

@Component
@Slf4j
public class KafkaDbTransactionSynchronizer {

  public void registerSynchronization(KafkaAction action,
                                      String messageKey,
                                      String entityId,
                                      BiConsumer<KafkaAction, String> sendKafkaMessage) {

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          int status = TransactionSynchronization.STATUS_UNKNOWN;

          @SneakyThrows
          @Override
          public void beforeCommit(boolean readOnly) {
            log.info(String.format(KAFKA_TRANSACTION_INITIALIZED,
                action, messageKey));
            sendKafkaMessage.accept(action, messageKey);
          }

          @Override
          public void afterCommit() {
            log.info(String.format(DATABASE_TRANSACTION_SUCCESSFUL,
                action, getDbIdLogText(action, entityId)));
            status = TransactionSynchronization.STATUS_COMMITTED;
          }

          @Override
          public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED) {
              log.info(String.format(
                  TRANSACTION_SUCCESSFUL, messageKey));

            } else {
              log.error(String.format(
                  DATABASE_TRANSACTION_FAILED,
                  action, getDbIdLogText(action, entityId)));
            }
          }
        }
    );
  }

  private String getDbIdLogText(KafkaAction action, String entityId) {
    return action.equals(KafkaAction.CREATE) ? ""
        : String.format(WITH_ENTITY_ID, entityId);
  }

}
