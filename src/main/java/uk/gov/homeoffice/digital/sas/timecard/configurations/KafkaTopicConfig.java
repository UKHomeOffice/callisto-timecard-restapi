package uk.gov.homeoffice.digital.sas.timecard.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("localhost")
public class KafkaTopicConfig {

  private final String topicName;

  public KafkaTopicConfig(@Value("${spring.kafka.template.default-topic}") String topicName) {
    this.topicName = topicName;
  }

  @Bean
  public NewTopic timecardTopicBuilder() {
    return TopicBuilder.name(topicName)
        .build();
  }
}
