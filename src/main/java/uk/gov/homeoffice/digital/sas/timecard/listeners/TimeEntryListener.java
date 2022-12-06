package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.producers.KafkaProducerTimeEntry;

@Slf4j
@Component
public class TimeEntryListener {

  private KafkaProducerTimeEntry kafkaProducerService;

  @Autowired
  public void createProducerService(KafkaProducerTimeEntry kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  @PostPersist
  private void sendKafkaMessageOnCreate(TimeEntry timeEntry) {
    kafkaProducerService.sendMessage(timeEntry, KafkaAction.CREATE);
  }

  @PostUpdate
  private void sendKafkaMessageOnUpdate(TimeEntry timeEntry) {
    kafkaProducerService.sendMessage(timeEntry, KafkaAction.UPDATE);
  }
}
