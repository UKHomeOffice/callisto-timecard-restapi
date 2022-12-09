package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.model.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
@Slf4j
public class KafkaProducerTimeEntry {

  private final KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate;

  public KafkaProducerTimeEntry(KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate) {
    this.kafkaTimeEntryTemplate = kafkaTimeEntryTemplate;
  }

  public void sendMessage(TimeEntry timeEntry, KafkaAction action) {
    KafkaEventMessage kafkaEventMessage = new KafkaEventMessage(timeEntry, action);
    ListenableFuture<SendResult<String, KafkaEventMessage>> future = kafkaTimeEntryTemplate.send(
        "callisto-timecard",
        timeEntry.getOwnerId().toString(),
        kafkaEventMessage
    );

    listenableFutureReporting(timeEntry, kafkaEventMessage, future);
  }

  private void listenableFutureReporting(
      TimeEntry timeEntry,
      KafkaEventMessage kafkaEventMessage,
      ListenableFuture<SendResult<String, KafkaEventMessage>> future
  ) {
    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onFailure(Throwable ex) {
        log.error(String.format("Sent message has failed=[ %s ]",
            kafkaEventMessage), ex);
      }

      @Override
      public void onSuccess(SendResult<String, KafkaEventMessage> result) {
        log.info(String.format("Sent message=[ %s ]", timeEntry));
      }
    });
  }

}
