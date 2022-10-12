package uk.gov.homeoffice.digital.sas.timecard.Producers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import java.util.Objects;

@Component
@Slf4j
public class KafkaProducerTimeEntry {

    @Autowired
    private KafkaTemplate<String, TimeEntry> kafkaTimeEntryTemplate;

    @Value(value = "${timeEntry.topic.name}")
    private String timeEntryTopic;

    public void sendMessage(TimeEntry message) {
        ListenableFuture<SendResult<String, TimeEntry>> future = kafkaTimeEntryTemplate.send(timeEntryTopic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, TimeEntry>>() {

            @Override
            public void onFailure(Throwable ex) {
                log.info(String.format("Sent message has failed=[ %s ]", Objects.toString(message)));
            }

            @Override
            public void onSuccess(SendResult<String, TimeEntry> result) {
                log.info(String.format("Sent message=[ %s ]", Objects.toString(message)));
            }
        });
    }

}
