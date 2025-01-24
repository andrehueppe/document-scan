package de.hueppe.example.scannerApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component

@AllArgsConstructor
public class KafkaDocumentProducer {

  private final KafkaTemplate<String, CheckResultEvent> kafkaTemplate;

  @Value("${kafka.topics.documents.v1}")
  private String topicName;

  @EventListener
  public void handleEvent(CheckResultEvent event) {
    sendDocument(event);
  }

  private void sendDocument(CheckResultEvent event) {
    kafkaTemplate.send(topicName, event);
  }
}