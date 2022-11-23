package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
@Slf4j
public class KafkaProducerTimeEntry {

  @Autowired
  private KafkaTemplate<String, TimeEntry> kafkaTimeEntryTemplate;

  @Value(value = "${timeEntry.topic.name}")
  private String timeEntryTopic;

  public void sendMessage(TimeEntry message) {
    try {
      kafkaTimeEntryTemplate.send(timeEntryTopic, message);
    } catch (Exception ex) {
      log.info(String.format("Sent message has failed=[ %s ]", message));
    }
  }

}
