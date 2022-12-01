package uk.gov.homeoffice.digital.sas.timecard.producers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import kafka.Kafka;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import uk.gov.homeoffice.digital.sas.jparest.config.JpaRestMvcConfig;
import uk.gov.homeoffice.digital.sas.jparest.web.SpelExpressionArgumentResolver;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EmbeddedKafka(topics = "callisto-timecard")
@ExtendWith(MockitoExtension.class)
public class KafkaProducerTimeEntryTest {

//  Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
//  Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new Serializer<String>(), new Serializer<String>()).createProducer();
//
//
//  @BeforeEach
//  void setUp(EmbeddedKafkaBroker embeddedKafkaBroker) {
//    this.embeddedKafkaBroker = embeddedKafkaBroker;
//  }

  @Mock
  private KafkaTemplate<String, JSONObject> kafkaTimeEntryTemplate;

  private KafkaProducerTimeEntry kafkaProducerTimeEntry = new KafkaProducerTimeEntry(kafkaTimeEntryTemplate);

//  @Autowired
//  @InjectMocks

//  @Spy
//  private JpaRestMvcConfig jpaRestMvcConfig = new JpaRestMvcConfig(objectMapper);
//
//  @Test
//  void addArgumentResolvers_shouldAddArgumentResolvers() {
//    List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
//    jpaRestMvcConfig.addArgumentResolvers(argumentResolvers);
//    assertThat(argumentResolvers).hasSize(1);
//    assertThat(argumentResolvers.get(0)).isInstanceOf(SpelExpressionArgumentResolver.class);
//  }
//
//  @Test
//  void givenKeyValue_whenSend_thenVerifyHistory() {
//
//    MockProducer mockProducer = new MockProducer<>();
//    KafkaProducer kafkaProducer = new KafkaProducer(mockProducer);
//    Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("soccer",
//        "{\"site\" : \"baeldung\"}");
//
//    assertTrue(mockProducer.history().size() == 1);
//  }

  @Test
  void sendMessage_createNewTimeEntry_messageIsSent() {
    TimeEntry timeEntry = createTimeEntry();

    JSONObject result = new JSONObject();
    result.put("schema", "blahblah");

//    KafkaTemplate mockKafkaTemplate = mock(KafkaTemplate.class);
//    when(mockKafkaTemplate.send("callisto-timecard", timeEntry.getOwnerId(), result)).equals("");

    kafkaProducerTimeEntry.sendMessage(timeEntry);

    Mockito.verify(kafkaTimeEntryTemplate).send("callisto-timecard", timeEntry.getOwnerId().toString(), result);

  }

  private TimeEntry createTimeEntry() {
    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);

    var timeEntry = new TimeEntry();
    timeEntry.setOwnerId(ownerId);
    timeEntry.setActualStartTime(getAsDate(actualStartTime));
    return timeEntry;
  }

  private Date getAsDate(LocalDateTime dateTime) {
    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
  }
}


//@SpringBootTest
//@Transactional
//public class KafkaProducerTimeEntryTest {
//
//  @Autowired
//  private TimeEntryRepository timeEntryRepository;
//
//  @PersistenceContext
//  private EntityManager entityManager;
//
//
//  @BeforeEach
//  void saveTimeEntry() {
//    saveEntryAndFlushDatabase(createTimeEntry(
//        OWNER_ID_1,
//        getAsDate(EXISTING_SHIFT_START_TIME)
//    ));
//  }
//
//
//  private Date getAsDate(LocalDateTime dateTime) {
//    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
//  }
//
//  private void saveEntryAndFlushDatabase(TimeEntry existingTimeEntry) {
//    Session session = entityManager.unwrap(Session.class);
//    session.setHibernateFlushMode(FlushMode.MANUAL);
//
//    timeEntryRepository.save(existingTimeEntry);
//
//    session.flush();
//    session.setHibernateFlushMode(FlushMode.AUTO);
//  }
//

//
//}
