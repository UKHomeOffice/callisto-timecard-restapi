package uk.gov.homeoffice.digital.sas.timecard.listeners;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

@ExtendWith(MockitoExtension.class)
class KafkaEntityListenerTest {

  private final static UUID OWNER_ID = UUID.randomUUID();

  private final static UUID TENANT_ID = UUID.randomUUID();
  private TimeEntry timeEntry;

  private String topicName = "timecard-test-topic";

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;
  private final TimeEntryKafkaEntityListener kafkaEntityListener =
      new TimeEntryKafkaEntityListener();

  @BeforeEach
  void setup() {
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    timeEntry = createTimeEntry(OWNER_ID, TENANT_ID, getAsDate(actualStartTime));
    TransactionSynchronizationManager.initSynchronization();
    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  void resolveMessageKey_timeEntryEntity_ownerIdAndTenantIdReturnedAsMessageKey() {
    assertThat(kafkaEntityListener.resolveMessageKey(timeEntry))
        .isEqualTo(generateMessageKey(timeEntry));
  }

  @Test
  void sendKafkaMessageOnCreate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnCreate(timeEntry, OWNER_ID.toString());

    Mockito.verify(kafkaProducerService)
        .sendMessage(generateMessageKey(timeEntry),
            TimeEntry.class,
            timeEntry,
            KafkaAction.CREATE);
  }

  @Test
  void sendKafkaMessageOnUpdate_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnUpdate(timeEntry, OWNER_ID.toString());

    Mockito.verify(kafkaProducerService)
        .sendMessage(OWNER_ID.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.UPDATE);
  }

  @Test
  void sendKafkaMessageOnDelete_timeEntryEntity_sendMessageMethodInvokedAsExpected() {
    kafkaEntityListener.sendKafkaMessageOnDelete(timeEntry, OWNER_ID.toString());

    Mockito.verify(kafkaProducerService)
        .sendMessage(OWNER_ID.toString(),
            TimeEntry.class,
            timeEntry,
            KafkaAction.DELETE);
  }

  @AfterEach
  void clear() {
    TransactionSynchronizationManager.clear();
  }

  private String generateMessageKey(TimeEntry timeEntry) {
    return timeEntry.getTenantId()  + ":" + timeEntry.getOwnerId();
  }

}