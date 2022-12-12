package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static uk.gov.homeoffice.digital.sas.timecard.utils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.utils.TimeEntryFactory.createTimeEntry;

import java.time.LocalDateTime;
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

  @Mock
  private ListenableFuture<SendResult<String, KafkaEventMessage>> responseFuture;

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE"})
  void sendMessage_actionOnTimeEntry_messageIsSentWithCorrectArguments(KafkaAction action) {

    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    TimeEntry timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));

    Mockito.when(kafkaTimeEntryTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(responseFuture);

    assertThatNoException().isThrownBy(() ->
        kafkaProducerTimeEntry.sendMessage(timeEntry, action));

    ArgumentCaptor<String> topicArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> ownerIdArgument = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<KafkaEventMessage> messageArgument =
        ArgumentCaptor.forClass(KafkaEventMessage.class);

    Mockito.verify(kafkaTimeEntryTemplate)
        .send(topicArgument.capture(), ownerIdArgument.capture(), messageArgument.capture());

    assertThat(topicArgument.getValue()).isEqualTo("callisto-timecard");
    assertThat(ownerIdArgument.getValue()).isEqualTo(timeEntry.getOwnerId().toString());
    assertThat(messageArgument.getValue().getResource()).isEqualTo(timeEntry);
    assertThat(messageArgument.getValue().getAction()).isEqualTo(action);
  }
}
