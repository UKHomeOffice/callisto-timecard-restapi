package uk.gov.homeoffice.digital.sas.timecard.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import uk.gov.homeoffice.digital.sas.jparest.models.BaseEntity;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;

@Component
@Slf4j
public class KafkaProducerService<T extends BaseEntity> {

  private final KafkaTemplate<String, KafkaEventMessage<T>> kafkaTemplate;

  public KafkaProducerService(
      KafkaTemplate<String, KafkaEventMessage<T>> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(Class<T> resourceType, T resource, KafkaAction action) {
    KafkaEventMessage<T> kafkaEventMessage =
        new KafkaEventMessage<>(resourceType, resource, action);
    ListenableFuture<SendResult<String, KafkaEventMessage<T>>> future =
        kafkaTemplate.send(
            "callisto-timecard",
            resource.getMessageKey(),
            kafkaEventMessage
        );

    listenableFutureReporting(resource, kafkaEventMessage, future);
  }

  private void listenableFutureReporting(
      T resource,
      KafkaEventMessage<T> kafkaEventMessage,
      ListenableFuture<SendResult<String, KafkaEventMessage<T>>> future
  ) {
    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onFailure(Throwable ex) {
        log.error(String.format("Sent message has failed=[ %s ]",
            kafkaEventMessage), ex);
      }

      @Override
      public void onSuccess(SendResult<String, KafkaEventMessage<T>> result) {
        log.info(String.format("Sent message=[ %s ]", resource));
      }
    });
  }
}
