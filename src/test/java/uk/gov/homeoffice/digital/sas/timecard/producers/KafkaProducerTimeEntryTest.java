package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.model.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@ExtendWith(SpringExtension.class)
class KafkaProducerTimeEntryTest {

  @Mock
  private KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate;

  @InjectMocks
  private KafkaProducerTimeEntry kafkaProducerTimeEntry;

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE"})
  void sendMessage_actionOnTimeEntry_messageIsSentWithCorrectArguments(KafkaAction action) {
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
  void sendMessage_actionOnTimeEntry_noExceptionThrown(KafkaAction action) {
    TimeEntry timeEntry = createTimeEntry();

    ListenableFuture<SendResult<String, KafkaEventMessage>> responseFuture =
        mock(ListenableFuture.class);
    Mockito.when(kafkaTimeEntryTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(responseFuture);

    assertThatNoException().isThrownBy(() ->
        kafkaProducerTimeEntry.sendMessage(timeEntry, action));
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
