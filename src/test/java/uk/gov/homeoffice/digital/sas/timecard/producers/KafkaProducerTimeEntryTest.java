package uk.gov.homeoffice.digital.sas.timecard.producers;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@EmbeddedKafka(topics = "callisto-timecard")
@ExtendWith(SpringExtension.class)
public class KafkaProducerTimeEntryTest {

  @Mock
  private KafkaTemplate<String, JSONObject> kafkaTimeEntryTemplate;

  private KafkaProducerTimeEntry kafkaProducerTimeEntry;

  @BeforeEach
  void setUp() {
    this.kafkaProducerTimeEntry = new KafkaProducerTimeEntry(this.kafkaTimeEntryTemplate);
  }

  @Test
  void sendMessage_whenNewTimeEntryCreated_messageIsSent() {
    TimeEntry timeEntry = createTimeEntry();

    var resource = new JSONObject();
    resource.put("schema", "blahblah");
    resource.put("content", timeEntry);

    var result = new JSONObject();
    result.put("action", "update");
    result.put("resource", resource);

    kafkaProducerTimeEntry.sendMessage(timeEntry);

    Mockito.verify(kafkaTimeEntryTemplate).send("callisto-timecard", timeEntry.getOwnerId().toString(), result);
  }

  private TimeEntry createTimeEntry() {
    UUID ownerId = UUID.fromString("ec703cac-de76-49c8-b1c4-83da6f8b42ce");
    LocalDateTime actualStartTime = LocalDateTime.of(
        2022, 1, 1, 9, 0, 0);

    var timeEntry = new TimeEntry();
    timeEntry.setOwnerId(ownerId);
    timeEntry.setActualStartTime(getAsDate(actualStartTime));
    return timeEntry;
  }

  private Date getAsDate(LocalDateTime dateTime) {
    return Date.from(dateTime.toInstant(ZoneOffset.UTC));
  }
}
