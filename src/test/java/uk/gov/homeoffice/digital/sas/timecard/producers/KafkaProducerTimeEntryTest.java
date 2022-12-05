package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.model.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@EmbeddedKafka(topics = "callisto-timecard")
@ExtendWith(SpringExtension.class)
public class KafkaProducerTimeEntryTest {

  @Mock
  private KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate;

  private KafkaProducerTimeEntry kafkaProducerTimeEntry;

  @BeforeEach
  void setUp() {
    this.kafkaProducerTimeEntry = new KafkaProducerTimeEntry(this.kafkaTimeEntryTemplate);
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE"})
  void sendMessage_actionOnTimeEntry_messageIsSentWithCorrectArguments(KafkaAction action) throws Exception {
    TimeEntry timeEntry = createTimeEntry();

    ListenableFuture<SendResult<String, KafkaEventMessage>> responseFuture =
        mock(ListenableFuture.class);
    Mockito.when(kafkaTimeEntryTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(responseFuture);

    kafkaProducerTimeEntry.sendMessage(timeEntry, action);

    ArgumentCaptor<String> topicArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> ownerIdArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<KafkaEventMessage> messageArgument =
        ArgumentCaptor.forClass(KafkaEventMessage.class);

    Mockito.verify(kafkaTimeEntryTemplate)
        .send(topicArgument.capture(), ownerIdArgument.capture(), messageArgument.capture());

    assertEquals("callisto-timecard", topicArgument.getValue());
    assertEquals(timeEntry.getOwnerId().toString(), ownerIdArgument.getValue());
    assertEquals(timeEntry, messageArgument.getValue().getResource());
    assertEquals(action, messageArgument.getValue().getAction());
  }

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE"})
  void sendMessage_timeEntryUpdated_noExceptionThrown(KafkaAction action) {
    TimeEntry timeEntry = createTimeEntry();

    ListenableFuture<SendResult<String, KafkaEventMessage>> responseFuture =
        mock(ListenableFuture.class);
    Mockito.when(kafkaTimeEntryTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(responseFuture);

    assertThatNoException().isThrownBy(() ->
        kafkaProducerTimeEntry.sendMessage(timeEntry, action));
  }

  @Test
  void sendMessage_sendReturnsNull_exceptionIsThrown() {
    TimeEntry timeEntry = createTimeEntry();

    Mockito.when(kafkaTimeEntryTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(null);

    Throwable thrown =
        catchThrowable(() -> kafkaProducerTimeEntry.sendMessage(timeEntry, KafkaAction.UPDATE));
    assertThat(thrown).isInstanceOf(Exception.class);
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
