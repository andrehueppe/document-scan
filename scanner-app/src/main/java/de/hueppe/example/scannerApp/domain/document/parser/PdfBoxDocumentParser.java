package de.hueppe.example.scannerApp.domain.document.parser;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter.BASE_PATH;

@Slf4j
@Component
public class PdfBoxDocumentParser implements DocumentParser {

  public static final String IBAN_REGEX = "\\b[A-Z]{2}\\d{2}[A-Z0-9]{4,30}\\b";
  private PDFTextStripper stripper;
  private PDDocument document;
  private String documentAsText;

  @Override
  public void init(String filePath) throws IOException {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      File loadedFile = new File(classLoader.getResource(BASE_PATH + filePath).getFile());
      document = PDDocument.load(loadedFile);

      PDFTextStripper stripper = new PDFTextStripper();
      documentAsText = stripper.getText(document);
    } catch (Exception exception) {
      log.error("Failed to initialize PDF document for content processing: {}", exception.getMessage());
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

  public void close() throws IOException {
    document.close();
  }
}
