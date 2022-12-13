package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${spring.kafka.template.default-topic}")
  private String topicName;

  private final KafkaTemplate<String, KafkaEventMessage<TimeEntry>> kafkaTimeEntryTemplate;

  public KafkaProducerTimeEntry(
      KafkaTemplate<String, KafkaEventMessage<TimeEntry>> kafkaTimeEntryTemplate) {
    this.kafkaTimeEntryTemplate = kafkaTimeEntryTemplate;
  }

  public void sendMessage(TimeEntry timeEntry, KafkaAction action) {
    KafkaEventMessage<TimeEntry> kafkaEventMessage =
        new KafkaEventMessage<>(TimeEntry.class, timeEntry, action);
    ListenableFuture<SendResult<String, KafkaEventMessage<TimeEntry>>> future =
        kafkaTimeEntryTemplate.send(
            topicName,
            timeEntry.getOwnerId().toString(),
            kafkaEventMessage
        );

    listenableFutureReporting(timeEntry, kafkaEventMessage, future);
  }

  private void listenableFutureReporting(
      TimeEntry timeEntry,
      KafkaEventMessage<TimeEntry> kafkaEventMessage,
      ListenableFuture<SendResult<String, KafkaEventMessage<TimeEntry>>> future
  ) {
    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onFailure(Throwable ex) {
        log.error(String.format("Sent message has failed=[ %s ]",
            kafkaEventMessage), ex);
      }

      @Override
      public void onSuccess(SendResult<String, KafkaEventMessage<TimeEntry>> result) {
        log.info(String.format("Sent message=[ %s ]", timeEntry));
      }
    });
  }

}
