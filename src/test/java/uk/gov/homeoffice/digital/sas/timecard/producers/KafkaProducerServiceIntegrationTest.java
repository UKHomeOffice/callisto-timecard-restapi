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
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;


@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties =
        { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaProducerServiceIntegrationTest<T> {

  @Autowired
  private KafkaProducerService<T> kafkaProducerService;

  @Autowired
  private TestKafkaConsumer<String> consumer;

  @Value("${projectVersion}")
  private String projectVersion;

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
            .await(20, TimeUnit.SECONDS);
    KafkaEventMessage<T> consumedMessage = gson.fromJson(consumer.getPayload(), KafkaEventMessage.class);

    assertThat(isMessageConsumed).isTrue();
    assertThat(consumedMessage.getAction()).isEqualTo(KAFKA_ACTION);
    assertThat(consumedMessage.getSchema()).contains(resource.getClass().getName()).contains(projectVersion);
    assertThat(consumer.getPayload()).contains(CommonUtils.timeEntryAsJsonString(resource));

  }

}
