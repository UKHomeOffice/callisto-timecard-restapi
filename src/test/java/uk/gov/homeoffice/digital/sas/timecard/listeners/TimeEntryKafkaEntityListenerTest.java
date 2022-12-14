package uk.gov.homeoffice.digital.sas.timecard.listeners;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@ExtendWith(MockitoExtension.class)
class TimeEntryKafkaEntityListenerTest {

  private final UUID ownerId = UUID.randomUUID();
  private TimeEntry timeEntry;

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;
  private final TimeEntryKafkaEntityListener kafkaEntityListener =
      new TimeEntryKafkaEntityListener();

  @BeforeEach
  void setup() {
    timeEntry = new TimeEntry();
    timeEntry.setOwnerId(ownerId);

    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  void resolveMessageKey_timeEntryEntity_useOwnerIdAsMessageKey() {
    assertThat(kafkaEntityListener.resolveMessageKey(timeEntry)).isEqualTo(ownerId.toString());
  }

  @Test
  void sendKafkaMessageOnCreate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnCreate(timeEntry);

    Mockito.verify(kafkaProducerService)
        .sendMessage(ownerId.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.CREATE);
  }

  @Test
  void sendKafkaMessageOnUpdate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnUpdate(timeEntry);

    Mockito.verify(kafkaProducerService)
        .sendMessage(ownerId.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.UPDATE);
  }
}