package uk.gov.homeoffice.digital.sas.timecard.Listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.Producers.KafkaProducerTimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

@Slf4j
@Component
public class TimeEntryListener {

    private KafkaProducerTimeEntry kafkaProducerService;

    @Autowired
    public void createProducerService(KafkaProducerTimeEntry kafkaProducerService){
        this.kafkaProducerService = kafkaProducerService;
    }

    @PrePersist
    private void prePersistEvent(TimeEntry entry) {
        log.info("Running prePersistEvent");

        kafkaProducerService.sendMessage(entry);
    }
}
