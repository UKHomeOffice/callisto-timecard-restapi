package uk.gov.homeoffice.digital.sas.timecard.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaDbTransactionSynchronizer;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import java.util.UUID;

import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

@ExtendWith(MockitoExtension.class)
class TimeEntryKafkaEntityListenerTest {

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private KafkaDbTransactionSynchronizer kafkaDbTransactionSynchronizer;

  @InjectMocks
  @Spy
  TimeEntryKafkaEntityListener timeEntryEntityListenerSpy;

  private TimeEntry timeEntry;

  private static final UUID ID = UUID.randomUUID();

  @BeforeEach
  void setup() {
    timeEntry = createTimeEntry();
  }

  @Test
  void sendMessageOnCreate_verifyMethodCall() {
    timeEntryEntityListenerSpy.sendMessageOnCreate(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnCreate(
            timeEntry, null);
  }

  @Test
  void sendMessageOnUpdate_verifyMethodCall() {
    timeEntry.setId(ID);
    timeEntryEntityListenerSpy.sendMessageOnUpdate(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnUpdate(timeEntry,
        timeEntry.getId().toString());
  }

  @Test
  void sendMessageOnDelete_verifyMethodCall() {
    timeEntry.setId(ID);
    timeEntryEntityListenerSpy.sendMessageOnDelete(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnDelete(timeEntry,
        timeEntry.getId().toString());
  }
}