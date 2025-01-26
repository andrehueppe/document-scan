package de.hueppe.example.scannerApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import de.hueppe.example.scannerApp.domain.document.service.DocumentValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
    "kafka.enabled=false"
})
class KafkaDocumentConsumerIT {

  @MockitoBean
  private DocumentValidationService documentValidationService;

  @Autowired
  private KafkaDocumentConsumer kafkaDocumentConsumer;

  @Test
  void should_handle_incoming_event() {
    CheckEvent event = new CheckEvent("http://example.com/file.pdf", "PDF");
    kafkaDocumentConsumer.consumeDocument(event);

    verify(documentValidationService).handleEvent(any(CheckEvent.class));
  }
}