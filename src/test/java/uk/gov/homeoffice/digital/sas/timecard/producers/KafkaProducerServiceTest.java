package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@ExtendWith(SpringExtension.class)
class KafkaProducerServiceTest {

  private final static String TOPIC_NAME = "callisto-timecard";

  @Mock
  private KafkaTemplate<String, KafkaEventMessage<TimeEntry>> kafkaTemplate;

  @InjectMocks
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private ListenableFuture<SendResult<String, KafkaEventMessage<TimeEntry>>> responseFuture;

  @ParameterizedTest
  @EnumSource(value = KafkaAction.class, names = {"CREATE", "UPDATE"})
  void sendMessage_actionOnResource_messageIsSentWithCorrectArguments(KafkaAction action) {
    ReflectionTestUtils.setField(kafkaProducerService, "topicName", TOPIC_NAME);
    ReflectionTestUtils.setField(kafkaProducerService, "projectVersion", "1.0.0");

    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    TimeEntry timeEntry = createTimeEntry(ownerId, getAsDate(actualStartTime));

    Mockito.when(kafkaTemplate.send(Mockito.any(), Mockito.any(), Mockito.any()))
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
}
