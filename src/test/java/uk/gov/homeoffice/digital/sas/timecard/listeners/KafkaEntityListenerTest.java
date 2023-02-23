package uk.gov.homeoffice.digital.sas.timecard.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils.getAsDate;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;

@ExtendWith(MockitoExtension.class)
class KafkaEntityListenerTest {

  private final static UUID OWNER_ID = UUID.randomUUID();

  private final static UUID TENANT_ID = UUID.randomUUID();
  private TimeEntry timeEntry;

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  private final TimeEntryKafkaEntityListener kafkaEntityListener =
      new TimeEntryKafkaEntityListener();

  @BeforeEach
  void setup() {
    kafkaEntityListener.createProducerService(kafkaProducerService);
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);
    timeEntry = createTimeEntry(OWNER_ID, TENANT_ID, getAsDate(actualStartTime));
    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  void resolveMessageKey_timeEntryEntity_ownerIdAndTenantIdReturnedAsMessageKey() {
    String messageKey = CommonUtils.generateMessageKey(timeEntry);
    assertThat(kafkaEntityListener.resolveMessageKey(timeEntry))
        .isEqualTo(messageKey);
  }

}