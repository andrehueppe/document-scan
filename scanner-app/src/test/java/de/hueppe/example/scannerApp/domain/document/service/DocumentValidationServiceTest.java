package de.hueppe.example.scannerApp.domain.document.service;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import com.sdase.malware.scanner.streaming.model.v1.CheckResultEvent;
import de.hueppe.example.scannerApp.domain.document.check.DocumentContentCheck;
import de.hueppe.example.scannerApp.domain.document.filter.DocumentPreprocessingFilter;
import de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter.PathTraversalException;
import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;

import static de.hueppe.example.scannerApp.domain.document.service.DocumentValidationService.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentValidationServiceTest {

  @Mock
  private DocumentPreprocessingFilter mockedFilter;

  @Mock
  private DocumentContentCheck mockedCheck;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private DocumentParser documentParser;

  private DocumentValidationService documentValidationService;

  @BeforeEach
  void setUp() {
    doNothing().when(documentParser).close();

    documentValidationService = new DocumentValidationService(
        Set.of(mockedFilter),
        Set.of(mockedCheck),
        eventPublisher,
        documentParser);
  }

  @Test
  void should_succeed_on_valid_document() {
    String testFileType = "application/pdf";
    String testUrl = "valid.pdf";
    when(mockedFilter.getName()).thenReturn("Mocked Filter");
    when(mockedCheck.getName()).thenReturn("Mocked Check");
    doNothing().when(mockedFilter).validate(testUrl);
    doNothing().when(mockedCheck).perform(eq(documentParser));


    documentValidationService.handleEvent(new CheckEvent(testUrl, testFileType));

    verify(eventPublisher, times(2)).publishEvent(any(CheckResultEvent.class));
    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(CheckResultEvent.StateEnum.OK, mockedFilter.getName(), PASSED_MESSAGE)));

    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(CheckResultEvent.StateEnum.OK, mockedCheck.getName(), PASSED_MESSAGE)));

    verify(documentParser).close();
  }

  @Test
  void should_fail_on_pre_processing_filter() {
    String testFileType = "application/pdf";
    String testUrl = "valid.pdf";
    when(mockedFilter.getName()).thenReturn("Mocked Filter");
    doThrow(new IllegalStateException("Invalid document mime type"))
        .when(mockedFilter)
        .validate(testUrl);

    documentValidationService.handleEvent(new CheckEvent(testUrl, testFileType));

    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(
            CheckResultEvent.StateEnum.ERROR,
            mockedFilter.getName(),
            PRE_PROCESSING_ERROR_MESSAGE + "Invalid document mime type")));

    verify(eventPublisher, times(1)).publishEvent(any(CheckResultEvent.class));
    verify(documentParser).close();
  }

  @Test
  void should_fail_on_blacklisted_iban() {
    String testFileType = "application/pdf";
    String testUrl = "valid.pdf";
    String testIban = "DE89370400440532013000";
    when(mockedFilter.getName()).thenReturn("Mocked Filter");
    when(mockedCheck.getName()).thenReturn("Mocked Check");
    doNothing().when(mockedFilter).validate(testUrl);
    doThrow(new IllegalStateException("IBAN is blacklisted: " + testIban))
        .when(mockedCheck)
        .perform(documentParser);

    documentValidationService.handleEvent(new CheckEvent(testUrl, testFileType));

    verify(eventPublisher, times(2)).publishEvent(any(CheckResultEvent.class));

    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(CheckResultEvent.StateEnum.OK, mockedFilter.getName(), PASSED_MESSAGE)));

    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(
            CheckResultEvent.StateEnum.SUSPICIOUS,
            mockedCheck.getName(),
            CONTENT_CHECK_FAILED_MESSAGE + "IBAN is blacklisted: DE89370400440532013000")));

    verify(documentParser).close();
  }

  @Test
  void should_fail_on_path_traversal() {
    String testFileType = "application/pdf";
    String testUrl = "../secret.file";
    when(mockedFilter.getName()).thenReturn("Mocked Filter");
    doThrow(new PathTraversalException("Path traversal detected: " + testUrl))
        .when(mockedFilter)
        .validate(testUrl);

    documentValidationService.handleEvent(new CheckEvent(testUrl, testFileType));

    verify(eventPublisher, times(1)).publishEvent(any(CheckResultEvent.class));

    verify(eventPublisher).publishEvent(
        eq(new CheckResultEvent(
            CheckResultEvent.StateEnum.SUSPICIOUS,
            mockedFilter.getName(),
            PRE_PROCESSING_ERROR_MESSAGE + "Path traversal detected: ../secret.file")));

    verify(documentParser).close();
  }
}