package uk.gov.homeoffice.digital.sas.timecard.producers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;


@SpringBootTest
@DirtiesContext
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class KafkaProducerServiceIntegrationTest<T> {

  @Autowired
  private KafkaProducerService<T> kafkaProducerService;

  @Autowired
  private TestKafkaConsumer<String> consumer;

  @Value("${schemaVersion}")
  private String schemaVersion;

  private final Gson gson = new GsonBuilder().create();
  private static final KafkaAction KAFKA_ACTION = KafkaAction.CREATE;

  @BeforeEach
  void setup() {
    consumer.resetLatch();
  }

  @Test
  void sendMessage_messageIsSentToTopic_messageExistsOnTopicAndContainsTheCorrectData() throws Exception {

    T resource = (T) createTimeEntry();
    kafkaProducerService.sendMessage("testMessageKey", (Class<T>) resource.getClass(), resource, KAFKA_ACTION);

    boolean isMessageConsumed = consumer.getLatch()
            .await(10, TimeUnit.SECONDS);
    KafkaEventMessage<T> consumedMessage = gson.fromJson(consumer.getPayload(), KafkaEventMessage.class);

    assertThat(isMessageConsumed).isTrue();
    assertThat(consumedMessage.getAction()).isEqualTo(KAFKA_ACTION);
    assertThat(consumedMessage.getSchema()).contains(resource.getClass().getName()).contains(schemaVersion);
    assertThat(consumer.getPayload()).contains(CommonUtils.objectAsJsonString(resource));

  }

}
