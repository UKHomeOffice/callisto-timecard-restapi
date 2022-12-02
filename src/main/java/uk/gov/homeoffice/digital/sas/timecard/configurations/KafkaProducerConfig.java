package uk.gov.homeoffice.digital.sas.timecard.configurations;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.gov.homeoffice.digital.sas.timecard.model.KafkaEventMessage;

@Configuration
public class KafkaProducerConfig {

  @Value("spring.kafka.bootstrap-servers")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, KafkaEventMessage> producerFactory() {

    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "30000");
    config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000000");
    config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "1000");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, KafkaEventMessage> kafkaTemplate(
      ProducerFactory<String, KafkaEventMessage> producerFactory
  ) {
    return new KafkaTemplate<>(producerFactory);
  }
}
