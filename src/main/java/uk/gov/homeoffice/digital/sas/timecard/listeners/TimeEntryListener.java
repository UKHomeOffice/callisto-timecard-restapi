package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.producers.KafkaProducerTimeEntry;

@Slf4j
@Component
public class TimeEntryListener {

  private KafkaProducerTimeEntry kafkaProducerService;
  private static final Logger LOGGER = LoggerFactory.getLogger(TimeEntryListener.class);

  @Autowired
  public void createProducerService(KafkaProducerTimeEntry kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  public void sendKafkaMessage(TimeEntry entry) {
    LOGGER.info("Running sendKafkaMessage");

    kafkaProducerService.sendMessage(entry);
  }
}
