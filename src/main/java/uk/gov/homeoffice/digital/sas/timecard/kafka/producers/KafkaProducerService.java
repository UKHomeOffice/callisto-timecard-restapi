package uk.gov.homeoffice.digital.sas.timecard.kafka.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;

@Slf4j
@Component
@EnableAutoConfiguration
public class KafkaProducerService<T> {
  private final KafkaTemplate<String, KafkaEventMessage<T>> kafkaTemplate;
  private final String topicName;
  private final String projectVersion;

  public KafkaProducerService(
      KafkaTemplate<String, KafkaEventMessage<T>> kafkaTemplate,
      @Value("${spring.kafka.template.default-topic}") String topicName,
      @Value("${projectVersion}") String projectVersion) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicName = topicName;
    this.projectVersion = projectVersion;
  }

  public void sendMessage(String messageKey, Class<T> resourceType,
                          T resource, KafkaAction action) {
    var kafkaEventMessage = new KafkaEventMessage<>(projectVersion, resourceType, resource, action);
    ListenableFuture<SendResult<String, KafkaEventMessage<T>>> future =
        kafkaTemplate.send(
            topicName,
            messageKey,
            kafkaEventMessage
        );

    listenableFutureReporting(kafkaEventMessage, future, messageKey);
  }

  private void listenableFutureReporting(
      KafkaEventMessage<T> kafkaEventMessage,
      ListenableFuture<SendResult<String, KafkaEventMessage<T>>> future,
      String messageKey
  ) {
    future.addCallback(new ListenableFutureCallback<>() {

      @Override
      public void onFailure(Throwable ex) {
        log.error(String.format("Message with key [ %s ] failed sending to topic [ %s ] with "
           + "action [%s]", messageKey, topicName, kafkaEventMessage.getAction(), ex));
      }

      @Override
      public void onSuccess(SendResult<String, KafkaEventMessage<T>> result) {
        log.info(String.format("Message with key [%s] sent to topic [ %s ] with action "
            + "[ %s ]", messageKey, topicName, kafkaEventMessage.getAction()));
      }
    });
  }
}
