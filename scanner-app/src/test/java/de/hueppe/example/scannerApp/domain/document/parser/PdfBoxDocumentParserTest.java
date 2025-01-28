package de.hueppe.example.scannerApp.domain.document.parser;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.hueppe.example.scannerApp.domain.document.parser.PdfBoxDocumentParser.FAILED_TO_INITIALIZE_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PdfBoxDocumentParserTest {

  private final static String TEST_DATA_FILE_NAME = "Testdaten_Rechnungseinreichung.pdf";
  private final PdfBoxDocumentParser documentParser = new PdfBoxDocumentParser();

  @Test
  void should_extract_iban() {
    documentParser.init(TEST_DATA_FILE_NAME);
    Optional<Iban> optionalIban = documentParser.extractIban();

    assertThat(optionalIban).isNotEmpty();
    assertThat(optionalIban.get().getValue()).isEqualTo("DE15300606010505780780");
  }

  @Test
  void should_fail_to_load() {
    IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> documentParser.init("unknown.txt"));
    assertThat(exception.getMessage()).startsWith(FAILED_TO_INITIALIZE_MESSAGE);
  }
}