package uk.gov.homeoffice.digital.sas.timecard.listeners;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaDbTransactionSynchronizer;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

@ExtendWith(MockitoExtension.class)
class TimeEntryKafkaEntityListenerTest {

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private KafkaDbTransactionSynchronizer kafkaDbTransactionSynchronizer;

  @Mock
  private TimeEntryKafkaEntityListener timeEntryKafkaEntityListener;

  @InjectMocks
  @Spy
  TimeEntryKafkaEntityListener timeEntryEntityListenerSpy;

  private TimeEntry timeEntry;


  @BeforeEach
  void setup() {
    timeEntry = createTimeEntry();
    TransactionSynchronizationManager.initSynchronization();
    timeEntryKafkaEntityListener.setProducerService(kafkaProducerService);
  }


  @Test
  void sendMessageOnCreate_verifyMethodCall() {
    timeEntryEntityListenerSpy.sendMessageOnCreate(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnCreate(timeEntry,
        timeEntry.getOwnerId().toString());
  }

  @Test
  void sendMessageOnUpdate_verifyMethodCall() {
    timeEntryEntityListenerSpy.sendMessageOnUpdate(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnUpdate(timeEntry,
        timeEntry.getOwnerId().toString());
  }

  @Test
  void sendMessageOnDelete_verifyMethodCall() {
    timeEntryEntityListenerSpy.sendMessageOnDelete(timeEntry);
    Mockito.verify((KafkaEntityListener) timeEntryEntityListenerSpy).sendKafkaMessageOnDelete(timeEntry,
        timeEntry.getOwnerId().toString());
  }

  @AfterEach
  void clear() {
    TransactionSynchronizationManager.clear();
  }

}