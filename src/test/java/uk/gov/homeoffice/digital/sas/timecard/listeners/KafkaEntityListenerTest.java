package uk.gov.homeoffice.digital.sas.timecard.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

@ExtendWith(MockitoExtension.class)
class KafkaEntityListenerTest {

  private final static UUID OWNER_ID = UUID.randomUUID();
  private TimeEntry timeEntry;

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;
  private final TimeEntryKafkaEntityListener kafkaEntityListener =
      new TimeEntryKafkaEntityListener();

  @BeforeEach
  void setup() {
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    timeEntry = createTimeEntry(OWNER_ID, getAsDate(actualStartTime));

    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  void resolveMessageKey_timeEntryEntity_ownerIdReturnedAsMessageKey() {
    assertThat(kafkaEntityListener.resolveMessageKey(timeEntry)).isEqualTo(OWNER_ID.toString());
  }

  @Test
  void sendKafkaMessageOnCreate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnCreate(timeEntry);

    Mockito.verify(kafkaProducerService)
        .sendMessage(OWNER_ID.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.CREATE);
  }

  @Test
  void sendKafkaMessageOnUpdate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnUpdate(timeEntry);

    Mockito.verify(kafkaProducerService)
        .sendMessage(OWNER_ID.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.UPDATE);
  }

  @Test
  void sendKafkaMessageOnDelete_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnDelete(timeEntry);

    Mockito.verify(kafkaProducerService)
        .sendMessage(OWNER_ID.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.DELETE);
  }

}