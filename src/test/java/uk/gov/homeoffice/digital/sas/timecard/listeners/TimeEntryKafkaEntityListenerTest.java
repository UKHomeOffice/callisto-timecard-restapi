package uk.gov.homeoffice.digital.sas.timecard.listeners;

import static org.mockito.Mockito.when;
import static uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory.createTimeEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@ExtendWith(MockitoExtension.class)
class TimeEntryKafkaEntityListenerTest {

  @Mock
  private KafkaProducerService<TimeEntry> kafkaProducerService;

  @Mock
  private Environment environment;

  private TimeEntry timeEntry;

  TimeEntryKafkaEntityListener entityListenerSpy;


  @BeforeEach
  void setup() {
    timeEntry = createTimeEntry();
    var entityListener = new TimeEntryKafkaEntityListener();
    entityListener.setProducerService(kafkaProducerService, environment);
    entityListenerSpy = Mockito.spy(entityListener);
    when(environment.getActiveProfiles()).thenReturn(new String[]{"localhost"});
  }


  @Test
  void sendMessageOnCreate_verifyMethodCall() {
    entityListenerSpy.sendMessageOnCreate(timeEntry);
    Mockito.verify((KafkaEntityListener) entityListenerSpy).sendKafkaMessageOnCreate(timeEntry);
  }

  @Test
  void sendMessageOnUpdate_verifyMethodCall() {
    entityListenerSpy.sendMessageOnUpdate(timeEntry);
    Mockito.verify((KafkaEntityListener) entityListenerSpy).sendKafkaMessageOnUpdate(timeEntry);
  }

  @Test
  void sendMessageOnDelete_verifyMethodCall() {
    entityListenerSpy.sendMessageOnDelete(timeEntry);
    Mockito.verify((KafkaEntityListener) entityListenerSpy).sendKafkaMessageOnDelete(timeEntry);
  }

}