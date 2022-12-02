package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.model.KafkaEventMessage;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
@Slf4j
public class KafkaProducerTimeEntry {

  private KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate;

  public KafkaProducerTimeEntry(KafkaTemplate<String, KafkaEventMessage> kafkaTimeEntryTemplate) {
    this.kafkaTimeEntryTemplate = kafkaTimeEntryTemplate;
  }

  public void sendMessage(TimeEntry timeEntry, KafkaAction action) throws Exception {
    try {
      KafkaEventMessage kafkaEventMessage = new KafkaEventMessage(timeEntry, action);
      kafkaTimeEntryTemplate.send(
          "callisto-timecard",
          timeEntry.getOwnerId().toString(),
          kafkaEventMessage
      );
    } catch (Exception ex) {
      log.info(String.format("Sent message has failed=[ %s ]", timeEntry));
      throw new Exception();
    }
  }

}
