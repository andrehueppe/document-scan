package de.hueppe.example.scannerApp.messaging;

import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent.StateEnum;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaDocumentProducerTest {

  @Mock
  private KafkaTemplate<String, CheckResultEvent> kafkaTemplate;

  private KafkaDocumentProducer producer;

  public static final String TOPIC_NAME = "test-topic";

  @BeforeEach
  void setUp() {
    producer = new KafkaDocumentProducer(kafkaTemplate, TOPIC_NAME);
  }

  @Test
  void should_emmit_kafka_event() {
    CheckResultEvent resultEvent = new CheckResultEvent(StateEnum.OK, "document-check", "Nothing found");
    producer.handleEvent(resultEvent);

    verify(kafkaTemplate).send(eq(TOPIC_NAME), eq(resultEvent));
  }
}