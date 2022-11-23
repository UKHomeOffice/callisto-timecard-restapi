package uk.gov.homeoffice.digital.sas.timecard.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.producers.KafkaProducerTimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import javax.persistence.PostPersist;

@Slf4j
@Component
public class TimeEntryListener {

    private KafkaProducerTimeEntry kafkaProducerService;

    @Autowired
    public void createProducerService(KafkaProducerTimeEntry kafkaProducerService){
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostPersist
    private void prePersistEvent(TimeEntry entry) {
        log.info("Running postPersistEvent");

        kafkaProducerService.sendMessage(entry);
    }
}