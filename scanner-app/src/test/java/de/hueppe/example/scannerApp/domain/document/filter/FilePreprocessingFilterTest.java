package de.hueppe.example.scannerApp.domain.document.filter;

import de.hueppe.example.scannerApp.domain.document.filter.FilePreprocessingFilter.PathTraversalException;
import de.hueppe.example.scannerApp.domain.document.mime.TikaMimeTypeDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class FilePreprocessingFilterTest {

  private final FilePreprocessingFilter fileFilter = new FilePreprocessingFilter(new TikaMimeTypeDetector());

  static Stream<Arguments> provideFileParameters() {
    return Stream.of(
        Arguments.of("unknown_file.pdf"),
        Arguments.of("invalid_mime.txt")
    );
  }

  @ParameterizedTest
  @MethodSource("provideFileParameters")
  void should_fail_on_invalid_mime_type(String givenFileName) {
    IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
        () -> fileFilter.validate(givenFileName));

    assertThat(illegalStateException.getMessage()).isEqualTo("Invalid document mime type");
  }

  @Test
  void should_fail_on_path_traversal() {
    String url = "../../secret.file";
    PathTraversalException pathTraversalException = assertThrows(PathTraversalException.class,
        () -> fileFilter.validate(url));

    assertThat(pathTraversalException.getMessage()).isEqualTo("Path traversal detected: " + url);
  }

  @Test
  void should_pass_on_valid_file() {
    try {
      fileFilter.validate("Testdaten_Rechnungseinreichung.pdf");
    } catch (Exception exception) {
      fail("Caught unexpected exception: " + exception.getMessage());
    }
  }
}