package de.hueppe.example.senderApp.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
@ConditionalOnProperty(value = "spring.kafka.enabled", matchIfMissing = true)
public class KafkaConfig {

}