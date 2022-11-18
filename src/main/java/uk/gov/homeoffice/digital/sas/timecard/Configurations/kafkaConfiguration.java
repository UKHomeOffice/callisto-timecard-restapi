package uk.gov.homeoffice.digital.sas.timecard.Configurations;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class kafkaConfiguration {

    @Bean
    public ProducerFactory<String, TimeEntry> producerFactory() {

        // Kafka Producer Configurations
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, TimeEntry> KafkaTemplate() {
        return new KafkaTemplate<String, TimeEntry>(producerFactory());
    }


// Consumer config
//    @Bean
//    public KafkaConsumer<String, TimeEntry> createKafkaConsumer() {
//
//        Properties props = new Properties();
//
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//            "b-2.callistodevmsk.nlo1o5.c2.kafka.eu-west-2.amazonaws.com:9094");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//
//        return new KafkaConsumer<>(props);
//    }
}
