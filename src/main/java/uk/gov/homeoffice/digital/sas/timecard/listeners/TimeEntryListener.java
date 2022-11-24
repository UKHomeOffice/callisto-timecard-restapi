package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
  @PostUpdate
  private void sendKafkaMessage(TimeEntry timeEntry) {
    log.info("Running postPersistEvent");
    kafkaProducerService.sendMessage(timeEntry);
  }
}
