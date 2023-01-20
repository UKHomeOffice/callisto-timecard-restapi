package uk.gov.homeoffice.digital.sas.timecard.configurations;

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
import uk.gov.homeoffice.digital.sas.timecard.kafka.KafkaEventMessage;

@Configuration
//@Profile("localhost")
public class KafkaProducerConfig<T> {

  private final String bootstrapServers;

  public KafkaProducerConfig(@Value("${kafka.bootstrap-server}") String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @Bean
  public ProducerFactory<String, KafkaEventMessage<T>> producerFactory() {

    Map<String, Object> config = Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
    );

    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, KafkaEventMessage<T>> kafkaTemplate(
      ProducerFactory<String, KafkaEventMessage<T>> producerFactory
  ) {
    return new KafkaTemplate<>(producerFactory);
  }
}
