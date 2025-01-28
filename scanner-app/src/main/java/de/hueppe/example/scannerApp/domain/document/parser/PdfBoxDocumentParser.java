package de.hueppe.example.scannerApp.domain.document.parser;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter.BASE_PATH;

@Slf4j
@Component
public class PdfBoxDocumentParser implements DocumentParser {

  public static final String IBAN_REGEX = "\\b[A-Z]{2}\\d{2}(?:\\s\\d{2,4}){4,7}\\b";
  public static final String FAILED_TO_INITIALIZE_MESSAGE = "Failed to initialize document for content processing: ";

  private PDFTextStripper stripper;
  private PDDocument document;
  private String documentAsText;

  @Override
  public void init(String filePath) {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream(BASE_PATH + filePath);

      if (inputStream == null) {
        throw new IllegalArgumentException("File not found: " + BASE_PATH + filePath);
      }

      document = PDDocument.load(inputStream);
      stripper = new PDFTextStripper();
      documentAsText = stripper.getText(document);
    } catch (Exception exception) {
      String message = FAILED_TO_INITIALIZE_MESSAGE + exception.getMessage();
      log.error(message);
      throw new IllegalStateException(message);
    }
  }

  @Override
  public Optional<Iban> extractIban() {
    Pattern pattern = Pattern.compile(IBAN_REGEX);
    Matcher matcher = pattern.matcher(documentAsText);

    if (matcher.find()) {
      return Optional.of(new Iban(matcher.group(0)));
    }

    return Optional.empty();
  }

  public void close() {
    try {
      document.close();
    } catch (IOException exception) {
      log.warn("Failed to close document");
    }
  }
}
