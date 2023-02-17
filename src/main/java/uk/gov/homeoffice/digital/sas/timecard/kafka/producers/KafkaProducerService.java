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
                          T resource, KafkaAction action) throws InterruptedException {
    var kafkaEventMessage = new KafkaEventMessage<>(projectVersion, resourceType, resource, action);
    CompletableFuture<SendResult<String, KafkaEventMessage<T>>> future = null;
    try {
      future = kafkaTemplate.send(
          topicName,
          messageKey,
          kafkaEventMessage
      );
      if (didKafkaCompleteSuccessfully(future)) {
        log.info(String.format(
            "Message with key [ %s ] sent to topic [ %s ] on partition [ %s ] with action [ %s ]",
            messageKey, topicName, future.get().getProducerRecord().partition(),
            kafkaEventMessage.getAction()));
      }
    } catch (ExecutionException e) {
      log.error(String.format("Message with key [ %s ] failed sending to topic [ %s ]."
                  + "action: [ %s ]", messageKey, topicName,
          kafkaEventMessage.getAction()), e);
    }
  }

  private boolean didKafkaCompleteSuccessfully(CompletableFuture<SendResult<String,
      KafkaEventMessage<T>>> future)
      throws ExecutionException, InterruptedException {
    return future.complete(future.get());
  }

}

