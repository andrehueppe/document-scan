package de.hueppe.example.scannerApp.domain.document.service;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import de.hueppe.example.scannerApp.domain.document.check.DocumentContentCheck;
import de.hueppe.example.scannerApp.domain.document.filter.DocumentPreprocessingFilter;
import de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter;
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

  private final Set<DocumentPreprocessingFilter> filterList;
  private final Set<DocumentContentCheck> documentCheckList;

  private final ApplicationEventPublisher eventPublisher;
  private final DocumentParser documentParser;

  @EventListener
  public void handleEvent(CheckEvent event) {
    log.info("Received new DocumentCheckEvent: {}", event);
    processDocument(event.getFileType(), event.getUrl());
  }

  private void processDocument(String fileType, String url) {
    CheckResultEvent resultEvent = CheckResultEvent.builder()
        .name(url)
        .build();

    /*
      Pre Processing steps
     */
    try {
      documentParser.init(url);
      filterList.forEach(check -> check.validate(fileType, url));
    } catch (FilePreprocessingFilter.PathTraversalException pathTraversalException) {
      resultEvent.toBuilder()
          .state(CheckResultEvent.StateEnum.SUSPICIOUS)
          .details("Document ignored checks due to pre processing error: " + pathTraversalException.getMessage())
          .build();
    } catch (Exception exception) {
      resultEvent.toBuilder()
          .state(CheckResultEvent.StateEnum.ERROR)
          .details("Document ignored checks due to pre processing error: " + exception.getMessage())
          .build();
    }

    /*
      Content processing steps
     */
    try {
      documentParser.init(url);
      documentCheckList.forEach(check -> check.check(documentParser));
    } catch (Exception exception) {
      resultEvent.toBuilder()
          .state(CheckResultEvent.StateEnum.SUSPICIOUS)
          .details("Document content check failed: " + exception.getMessage())
          .build();
    }

    eventPublisher.publishEvent(resultEvent);
  }
}
