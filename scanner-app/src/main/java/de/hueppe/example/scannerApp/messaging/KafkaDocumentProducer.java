package de.hueppe.example.scannerApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaDocumentProducer {

  private final KafkaTemplate<String, CheckResultEvent> kafkaTemplate;
  private final String topicName;

  @EventListener
  public void handleEvent(CheckResultEvent event) {
    log.info("Sending check result: {}", event);
    sendDocument(event);
  }

  private void sendDocument(CheckResultEvent event) {
    kafkaTemplate.send(topicName, event);
  }
}