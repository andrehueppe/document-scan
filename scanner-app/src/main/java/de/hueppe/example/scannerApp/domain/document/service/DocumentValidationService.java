package de.hueppe.example.scannerApp.domain.document.service;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import de.hueppe.example.scannerApp.domain.document.filter.DocumentPreprocessingFilter;
import de.hueppe.example.scannerApp.domain.document.iban.IbanBlackListProvider;
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
  private final ApplicationEventPublisher eventPublisher;
  private final IbanBlackListProvider ibanBlackListProvider;

  @EventListener
  public void handleEvent(CheckEvent event) {
    log.info("Received new DocumentCheckEvent: {}", event);
    processDocument(event.getFileType(), event.getUrl());
  }

  private void processDocument(String fileType, String url) {
    CheckResultEvent resultEvent = CheckResultEvent.builder()
        .name(url)
        .build();

    try {
      boolean result = filterList.stream()
          .map(filter -> filter.validate(fileType, url))
          .anyMatch(filterResult -> {
            if (!filterResult) {
              resultEvent.toBuilder()
                  .state(CheckResultEvent.StateEnum.IGNORED)
                  .details("Document ignored checks due to pre processing error.")
                  .build();

              log.debug("Preprocessing filter failed for: {}", url);
              return true;
            }
            resultEvent.toBuilder()
                .state(CheckResultEvent.StateEnum.OK)
                .details("Document passed pre processing checks.")
                .build();
            return false;
          });

      if (!result) {
        log.info("Document check stopped for {} due to previous errors.", url);
        return;
      }

      //TODO: Implement further parsing/processing of given document
      // - Make use of state CheckResultEvent.StateEnum.SUSPICIOUS
      // - Make use of IbanBlackListProvider

    } catch (Exception exception) {
      log.error("Processing of document {} failed.", url);
      resultEvent.toBuilder()
          .state(CheckResultEvent.StateEnum.ERROR)
          .details("Document failed due to technical error.")
          .build();
    }

    eventPublisher.publishEvent(resultEvent);
  }
}
