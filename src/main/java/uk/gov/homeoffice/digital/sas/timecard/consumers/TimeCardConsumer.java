package uk.gov.homeoffice.digital.sas.timecard.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Service
@Slf4j
public class TimeCardConsumer {

    @KafkaListener(topics = "timeEntry-accruals-topic", groupId = "group_id")

    public void consume (String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TimeEntry timeEntry = mapper.readValue(message, TimeEntry.class);
        log.info("consuming message =" + message);

    }
}
