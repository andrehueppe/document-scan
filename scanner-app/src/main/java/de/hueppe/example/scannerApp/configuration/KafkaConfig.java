package de.hueppe.example.scannerApp.configuration;

import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import de.hueppe.example.scannerApp.messaging.KafkaDocumentProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "spring.kafka.enabled", matchIfMissing = true)
public class KafkaConfig {

  @Value("${spring.kafka.topics.documents.v1}")
  private String topicName;

  @Bean
  public KafkaDocumentProducer kafkaDocumentProducer(KafkaTemplate<String, CheckResultEvent> kafkaTemplate) {
    return new KafkaDocumentProducer(kafkaTemplate, topicName);
  }
}
