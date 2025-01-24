package de.hueppe.example.scannerApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDocumentConsumer {

    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "demo-documents-v1", groupId = "scanner-group")
    public void consumeDocument(CheckEvent event) {
      log.info("Received document event: {}", event);
      eventPublisher.publishEvent(new CheckDocumentEvent(event.getFileType(), event.getUrl()));
    }

    public record CheckDocumentEvent(String fileType, String url) {};
}
