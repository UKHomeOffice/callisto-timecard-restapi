package uk.gov.homeoffice.digital.sas.timecard.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class KafkaDbTransactionSynchronizer {

    public void registerSynchronization(KafkaAction action,
                                        String messageKey,
                                        String ownerId,
                                        BiConsumer<KafkaAction, String> sendKafkaMessage) {

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                int status = TransactionSynchronization.STATUS_UNKNOWN;

                @Override
                public void beforeCommit(boolean readOnly) {
                    log.info(String.format("Kafka Transaction [ %s ] Initialized with message key [ %s ]",
                            action, messageKey));
                    sendKafkaMessage.accept(action, messageKey);
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
