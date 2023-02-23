package uk.gov.homeoffice.digital.sas.timecard.producers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class TestKafkaConsumer<T> {

    @Getter
    private CountDownLatch latch = new CountDownLatch(1);

    @Getter
    private T payload;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    public void receive(ConsumerRecord<?, T> consumerRecord) {
        payload = consumerRecord.value();
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

}
