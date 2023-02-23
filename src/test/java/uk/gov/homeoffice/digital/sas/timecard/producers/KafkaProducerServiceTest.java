package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.generateMessageKey;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.MESSAGE_FAILED_SENDING_TO_TOPIC;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TestConstants.MESSAGE_SENT_TO_TOPIC_CALLISTO;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093",
    "port=9093"})
class KafkaProducerServiceTest {

  private final static String TOPIC_NAME = "callisto-timecard";

  private UUID ownerId;

  private TimeEntry timeEntry;

  private String messageKey;

  @Mock
  private KafkaTemplate<String, KafkaEventMessage<TimeEntry>> kafkaTemplate;

  @InjectMocks
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private CompletableFuture<SendResult<String, KafkaEventMessage<TimeEntry>>> responseFuture;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(kafkaProducerService, "topicName", TOPIC_NAME);
    ReflectionTestUtils.setField(kafkaProducerService, "projectVersion", "1.0.0");
    ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));
    messageKey = generateMessageKey(timeEntry);
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class)
  void sendMessage_actionOnResource_messageIsSentWithCorrectArguments(KafkaAction action) {
    when(kafkaTemplate.send(any(), any(), any()))
        .thenReturn(responseFuture);

    assertThatNoException().isThrownBy(() ->
        kafkaProducerService.sendMessage(ownerId.toString(), TimeEntry.class, timeEntry, action));

    ArgumentCaptor<String> topicArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> ownerIdArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<KafkaEventMessage<TimeEntry>> messageArgument =
        ArgumentCaptor.forClass(KafkaEventMessage.class);

    Mockito.verify(kafkaTemplate)
        .send(topicArgument.capture(), ownerIdArgument.capture(), messageArgument.capture());

    assertThat(topicArgument.getValue()).isEqualTo(TOPIC_NAME);
    assertThat(ownerIdArgument.getValue()).isEqualTo(timeEntry.getOwnerId().toString());
    assertThat(messageArgument.getValue().getSchema()).isEqualTo(
        TimeEntry.class.getCanonicalName() + ", 1.0.0");
    assertThat(messageArgument.getValue().getResource()).isEqualTo(timeEntry);
    assertThat(messageArgument.getValue().getAction()).isEqualTo(action);
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class)
  void sendMessage_actionOnResource_onFailureMessageLogged(KafkaAction action) throws ExecutionException, InterruptedException {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    responseFuture = mock(CompletableFuture.class);

    when(kafkaTemplate.send(any(), any(), any()))
        .thenReturn(responseFuture);
    Mockito.doThrow(ExecutionException.class).when(responseFuture).get();

    kafkaProducerService.sendMessage(messageKey, TimeEntry.class, timeEntry, action);
    assertThat(responseFuture.isDone()).isFalse();
    assertThat(String.format(
        MESSAGE_FAILED_SENDING_TO_TOPIC,
        messageKey, action.toString().toLowerCase())).isEqualTo(logList.get(0).getMessage());
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class)
  void sendMessage_actionOnResource_onFailureInterruptLogged(KafkaAction action)
      throws ExecutionException, InterruptedException {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    responseFuture = mock(CompletableFuture.class);

    when(kafkaTemplate.send(any(), any(), any()))
        .thenReturn(responseFuture);
    Mockito.doThrow(InterruptedException.class).when(responseFuture).get();

    kafkaProducerService.sendMessage(messageKey, TimeEntry.class, timeEntry, action);
    assertThat(responseFuture.isDone()).isFalse();
    assertThat(String.format(
        MESSAGE_FAILED_SENDING_TO_TOPIC,
        messageKey, action.toString().toLowerCase())).isEqualTo(logList.get(0).getMessage());
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class)
  void sendMessage_actionOnResource_onSuccessMessageLogged(KafkaAction action) throws InterruptedException, ExecutionException {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    responseFuture = spy(CompletableFuture.class);
    SendResult<String, KafkaEventMessage<TimeEntry>> sendResult = mock(SendResult.class);

    when(kafkaTemplate.send(any(), any(), any())).thenReturn(responseFuture);
    when(responseFuture.complete(sendResult)).thenReturn(true);
    assertThat(responseFuture.isDone()).isTrue();

    kafkaProducerService.sendMessage(messageKey, TimeEntry.class, timeEntry,
        action);

    assertThat(String.format(
        MESSAGE_SENT_TO_TOPIC_CALLISTO,
        messageKey, action.toString().toLowerCase())).isEqualTo(logList.get(0).getMessage());

  }

  private static ListAppender<ILoggingEvent> getLoggingEventListAppender() {
    Logger kafkaLogger = (Logger) LoggerFactory.getLogger(KafkaProducerService.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    kafkaLogger.addAppender(listAppender);
    return listAppender;
  }
}
