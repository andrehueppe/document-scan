package de.hueppe.example.scannerApp.domain.validation.service;

import de.hueppe.example.scannerApp.domain.validation.filter.DocumentPreprocessingFilter;
import de.hueppe.example.scannerApp.messaging.KafkaDocumentConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentValidationService {

  private final Set<DocumentPreprocessingFilter> filterList;

  @EventListener
  void handleEvent(KafkaDocumentConsumer.CheckDocumentEvent event) {
    log.trace("Received new DocumentCheckEvent: {}", event);
  }
}
