package uk.gov.homeoffice.digital.sas.timecard.producers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.generateMessageKey;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

@ExtendWith(SpringExtension.class)
class KafkaProducerServiceTest {

  private final static String TOPIC_NAME = "callisto-timecard";

  @Mock
  private KafkaTemplate<String, KafkaEventMessage<TimeEntry>> kafkaTemplate;

  @InjectMocks
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private CompletableFuture<SendResult<String, KafkaEventMessage<TimeEntry>>> responseFuture;

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE", "DELETE"})
  void sendMessage_actionOnResource_messageIsSentWithCorrectArguments(KafkaAction action) {
    ReflectionTestUtils.setField(kafkaProducerService, "topicName", TOPIC_NAME);
    ReflectionTestUtils.setField(kafkaProducerService, "projectVersion", "1.0.0");

    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    TimeEntry timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));

    Mockito.when(kafkaTemplate.send(any(), any(), any()))
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
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE", "DELETE"})
  void listenableFutureReporting_actionOnResource_onFailureMessageLogged(KafkaAction action) throws InterruptedException {
    ReflectionTestUtils.setField(kafkaProducerService, "topicName", TOPIC_NAME);
    ReflectionTestUtils.setField(kafkaProducerService, "projectVersion", "1.0.0");

    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    TimeEntry timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));
    String messageKey = generateMessageKey(timeEntry);

    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    responseFuture = mock(CompletableFuture.class);
    Throwable throwable = mock(Throwable.class);

    Mockito.when(kafkaTemplate.send(any(), any(), any()))
        .thenReturn(responseFuture);
    //doAnswer(invocationOnMock -> {
    //  CompletableFutureCallback listenableFutureCallback = invocationOnMock.getArgument(0);
    //  listenableFutureCallback.onFailure(throwable);
    //  return null;
    //}).when(responseFuture).addCallback(any(ListenableFutureCallback.class));

    kafkaProducerService.sendMessage(messageKey, TimeEntry.class, timeEntry, action);

    assertEquals(String.format(
        "Message with key [ %s ] failed sending to topic [ callisto-timecard ] with action [ %s ]",
        messageKey, action.toString().toLowerCase()), logList.get(0).getMessage());
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE", "DELETE"})
  void can_publishDataToKafka(KafkaAction action) throws InterruptedException {
    ReflectionTestUtils.setField(kafkaProducerService, "topicName", TOPIC_NAME);
    ReflectionTestUtils.setField(kafkaProducerService, "projectVersion", "1.0.0");

    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    TimeEntry timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));
    String messageKey = generateMessageKey(timeEntry);

    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    responseFuture = mock(CompletableFuture.class);
    SendResult<String, Object> sendResult = mock(SendResult.class);

    //Mockito.when(kafkaTemplate.send(any(), any(), any()))
    //    .thenReturn(responseFuture);
    //doAnswer(invocationOnMock -> {
    //  CompletableFuture completableFuture = invocationOnMock.getArgument(0);
    //  completableFuture.whenComplete(responseFuture, ex);
    //  return null;
    //});

    kafkaProducerService.sendMessage(messageKey, TimeEntry.class, timeEntry, action);

    responseFuture.completeExceptionally(new Exception("Error"));

    assertEquals(String.format(
        "Message with key [ %s ] sent to topic [ callisto-timecard ] with action [ %s ]",
        messageKey, action.toString().toLowerCase()), logList.get(0).getMessage());

  }

  @NotNull
  private static ListAppender<ILoggingEvent> getLoggingEventListAppender() {
    Logger kafkaLogger = (Logger) LoggerFactory.getLogger(KafkaProducerService.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    kafkaLogger.addAppender(listAppender);
    return listAppender;
  }
}
