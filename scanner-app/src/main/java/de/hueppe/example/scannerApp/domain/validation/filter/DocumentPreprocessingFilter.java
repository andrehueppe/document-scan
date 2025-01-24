package de.hueppe.example.scannerApp.domain.validation.filter;

import de.hueppe.example.scannerApp.messaging.KafkaDocumentConsumer;

public interface DocumentPreprocessingFilter {

  boolean validate(KafkaDocumentConsumer.CheckDocumentEvent event);

}
