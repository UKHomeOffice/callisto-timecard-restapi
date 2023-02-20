package uk.gov.homeoffice.digital.sas.timecard.kafka.producers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
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
                          T resource, KafkaAction action) throws InterruptedException, ExecutionException {
    var kafkaEventMessage = new KafkaEventMessage<>(projectVersion, resourceType, resource, action);
    CompletableFuture<SendResult<String, KafkaEventMessage<T>>> future = null;
    try {
      future = kafkaTemplate.send(
          topicName,
          messageKey,
          kafkaEventMessage
      );
      completeKafkaTransaction(future);
      logKafkaMessage(messageKey, kafkaEventMessage, future);
    } catch (InterruptedException e) {
      log.error(String.format("Message with key [ %s ] failed sending to topic [ %s ]."
              + "action: [ %s ]", messageKey, topicName,
          kafkaEventMessage.getAction()), e);
    }
  }

  private void logKafkaMessage(
      String messageKey,
      KafkaEventMessage<T> kafkaEventMessage,
      CompletableFuture<SendResult<String,
          KafkaEventMessage<T>>> future) {
    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info(String.format(
            "Message with key [ %s ] sent to topic [ %s ] on partition [ %s ] with action [ %s ]",
            messageKey, topicName, result.getProducerRecord().partition(),
            kafkaEventMessage.getAction()));
      }
    });
  }

  private void completeKafkaTransaction(
      CompletableFuture<SendResult<String, KafkaEventMessage<T>>> future)
      throws ExecutionException, InterruptedException {
    future.complete(future.get());
  }
}

