package uk.gov.homeoffice.digital.sas.timecard.listeners;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.homeoffice.digital.sas.timecard.enums.KafkaAction;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;

@Component
public abstract class KafkaEntityListener {

  private KafkaProducerService<Object> kafkaProducerService;

  public abstract String resolveMessageKey(Object resource) throws Exception;

  @Autowired
  public void createProducerService(KafkaProducerService<Object> kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  @PostPersist
  private void sendKafkaMessageOnCreate(Object resource) throws Exception {

    kafkaProducerService.sendMessage(resolveMessageKey(resource),
        (Class<Object>) resource.getClass(), resource, KafkaAction.CREATE);
  }

  @PostUpdate
  private void sendKafkaMessageOnUpdate(Object resource) throws Exception {
    kafkaProducerService.sendMessage(resolveMessageKey(resource),
        (Class<Object>) resource.getClass(), resource, KafkaAction.UPDATE);
  }
}
