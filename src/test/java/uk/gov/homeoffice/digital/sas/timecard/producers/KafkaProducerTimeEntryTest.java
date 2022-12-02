package uk.gov.homeoffice.digital.sas.timecard.producers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

  @Test
  void sendMessage_newTimeEntryCreated_messageIsSent() throws Exception {
    TimeEntry timeEntry = createTimeEntry();

    kafkaProducerTimeEntry.sendMessage(timeEntry, KafkaAction.CREATE);

    KafkaEventMessage result = new KafkaEventMessage(timeEntry, KafkaAction.CREATE);

    ArgumentCaptor<KafkaEventMessage> argument = ArgumentCaptor.forClass(KafkaEventMessage.class);
    Mockito.verify(kafkaTimeEntryTemplate).send("callisto-timecard", timeEntry.getOwnerId().toString(), argument.capture());
    assertEquals("John", argument.getValue().getAction());
  }

  @Test
  void sendMessage_timeEntryUpdated_messageIsSent() throws Exception {
    TimeEntry timeEntry = createTimeEntry();

    kafkaProducerTimeEntry.sendMessage(timeEntry, KafkaAction.UPDATE);

    KafkaEventMessage result = new KafkaEventMessage(timeEntry, KafkaAction.UPDATE);
    Mockito.verify(kafkaTimeEntryTemplate).send("callisto-timecard", timeEntry.getOwnerId().toString(), result);
  }

//  @Test
//  void sendMessage_sendErrors_exceptionIsThrown() throws Exception {
//    TimeEntry timeEntry = createTimeEntry();
//
//    KafkaEventMessage result = new KafkaEventMessage(timeEntry, KafkaAction.UPDATE);
//
//    Mockito.when(kafkaTimeEntryTemplate.send("callisto-timecard", timeEntry.getOwnerId().toString(),
//        result)).thenThrow();
//    Assertions.assertThrows(Exception.class).isThrownBy(
//        kafkaProducerTimeEntry.sendMessage(timeEntry, KafkaAction.UPDATE));
//  }

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
