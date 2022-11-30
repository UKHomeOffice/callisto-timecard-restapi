package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Component
@Slf4j
public class KafkaProducerTimeEntry {

  @Autowired
  private KafkaTemplate<String, JSONObject> kafkaTimeEntryTemplate;

  public void sendMessage(TimeEntry timeEntry) {
    try {
      var message = generateKafkaWrapper(timeEntry);
      kafkaTimeEntryTemplate.send("callisto-timecard", timeEntry.getOwnerId().toString(), message);
    } catch (Exception ex) {
      log.info(String.format("Sent message has failed=[ %s ]", timeEntry));
    }
  }

  private JSONObject generateKafkaWrapper(TimeEntry timeEntry) {
    var resource = new JSONObject();
    resource.put("schema", "blahblah");
    resource.put("content", timeEntry);

    var result = new JSONObject();
    result.put("action", "update");
    result.put("resource", resource);

    return result;
  }

}
