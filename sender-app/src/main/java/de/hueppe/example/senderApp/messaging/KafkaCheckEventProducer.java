package de.hueppe.example.senderApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCheckEventProducer {

    private final KafkaTemplate<String, CheckEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.documents.v1}")
    private String topicName;

    public void sendCheckEvent(CheckEvent checkEvent) {
        kafkaTemplate.send(topicName, checkEvent);
    }
}
