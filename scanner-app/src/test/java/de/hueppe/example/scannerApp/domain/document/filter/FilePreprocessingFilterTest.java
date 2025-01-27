package de.hueppe.example.scannerApp.domain.document.filter;

import de.hueppe.example.scannerApp.domain.document.mime.TikaMimeTypeDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FilePreprocessingFilterTest {

  private final FilePreprocessingFilter fileFilter = new FilePreprocessingFilter(new TikaMimeTypeDetector());

  @Test
  void should_fail_on_missing_file() {
    assertThat(fileFilter.validate("application/pdf", "unknown_file.pdf")).isFalse();
  }

  @Test
  void should_fail_on_path_traversing() {
    assertThat(fileFilter.validate("application/pdf", "../../secret.file")).isFalse();
  }

  @Test
  void should_fail_on_invalid_file_mimetype() {
    assertThat(fileFilter.validate("application/pdf", "invalid_mime.pdf")).isFalse();
  }

  @Test
  void should_fail_on_missing_whitelist_entry() {
    assertThat(fileFilter.validate("plain/text", "invalid.txt")).isFalse();
  }

  @Test
  void should_pass_on_valid_file() {
    assertThat(fileFilter.validate("application/pdf", "valid.pdf")).isTrue();
  }
}