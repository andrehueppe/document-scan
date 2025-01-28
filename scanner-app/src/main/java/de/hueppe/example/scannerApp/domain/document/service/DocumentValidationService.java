package de.hueppe.example.scannerApp.domain.document.service;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent.CheckResultEventBuilder;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent.StateEnum;
import de.hueppe.example.scannerApp.domain.document.check.DocumentContentCheck;
import de.hueppe.example.scannerApp.domain.document.filter.DocumentPreprocessingFilter;
import de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter.PathTraversalException;
import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentValidationService {

  public static final String PRE_PROCESSING_ERROR_MESSAGE = "Document ignored checks due to pre processing error: ";
  public static final String CONTENT_CHECK_FAILED_MESSAGE = "Document content check failed: ";
  public static final String PASSED_MESSAGE = "PASSED";

  private final Set<DocumentPreprocessingFilter> filterList;
  private final Set<DocumentContentCheck> documentCheckList;

  private final ApplicationEventPublisher eventPublisher;
  private final DocumentParser documentParser;

  @EventListener
  public void handleEvent(CheckEvent event) {
    log.debug("Received new DocumentCheckEvent: {}", event);
    processDocument(event.getUrl());
  }

  private void processDocument(String url) {
    CheckResultEventBuilder eventBuilder = CheckResultEvent.builder();

    /*
      Pre Processing steps
     */
    filterList.forEach(filter -> {
      eventBuilder.name(filter.getName());

      try {
        filter.validate(url);
        eventBuilder
            .state(StateEnum.OK)
            .details(PASSED_MESSAGE);
      } catch (PathTraversalException pathTraversalException) {
        eventBuilder
            .state(StateEnum.SUSPICIOUS)
            .details(PRE_PROCESSING_ERROR_MESSAGE + pathTraversalException.getMessage());
      } catch (Exception exception) {
        eventBuilder
            .state(StateEnum.ERROR)
            .details(PRE_PROCESSING_ERROR_MESSAGE + exception.getMessage());
      } finally {
        CheckResultEvent resultEvent = eventBuilder.build();
        log.info("Finished filter: {}", resultEvent);
        eventPublisher.publishEvent(resultEvent);
      }
    });

    /*
      Content processing steps
     */
    documentParser.init(url);

    documentCheckList.forEach(check -> {
      eventBuilder.name(check.getName());

      try {
        check.perform(documentParser);
        eventBuilder
            .state(StateEnum.OK)
            .details(PASSED_MESSAGE);
      } catch (Exception exception) {
        eventBuilder
            .state(StateEnum.SUSPICIOUS)
            .details(CONTENT_CHECK_FAILED_MESSAGE + exception.getMessage());
      } finally {
        CheckResultEvent resultEvent = eventBuilder.build();
        log.info("Finished check: {}", resultEvent);
        eventPublisher.publishEvent(resultEvent);
      }
    });

    documentParser.close();
  }
}
