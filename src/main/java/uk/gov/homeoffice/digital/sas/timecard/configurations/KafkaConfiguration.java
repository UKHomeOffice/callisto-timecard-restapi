package uk.gov.homeoffice.digital.sas.timecard.configurations;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

@Configuration
public class KafkaConfiguration {

  @Bean
  public ProducerFactory<String, TimeEntry> producerFactory() {

    // Kafka Producer Configurations
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        "b-1.callistodevmsk.nlo1o5.c2.kafka.eu-west-2.amazonaws.com:9094");
    config.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "30000");
    config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000000");
    config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "1000");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, TimeEntry> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

}
