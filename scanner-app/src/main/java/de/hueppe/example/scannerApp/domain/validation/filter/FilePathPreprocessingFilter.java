package de.hueppe.example.scannerApp.domain.validation.filter;

import de.hueppe.example.scannerApp.messaging.KafkaDocumentConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Order(1)
@Component
public class FilePathPreprocessingFilter implements DocumentPreprocessingFilter {

  public static final List<String> FILE_EXTENSION_WHITELIST = List.of("pdf", "txt");
  private static final String BASE_PATH = "testData/";

  @Override
  public boolean validate(KafkaDocumentConsumer.CheckDocumentEvent event) {
    return isFileTypeAcceptable(event.fileType(), event.url())
        && isValidFilePath(event.url());
  }

  private boolean isFileTypeAcceptable(String fileType, String url) {
    try {
      String probeContentType = Files.probeContentType(Paths.get(url));
      return FILE_EXTENSION_WHITELIST.contains(probeContentType)
          && givenExtensionEqualsProbe(fileType, probeContentType);
    } catch (IOException e) {
      return false;
    }
  }

  private boolean isValidFilePath(String filePath) {
    try {
      Path path = Paths.get(BASE_PATH + filePath).normalize();
      log.debug("Attempt to validate new file path {}", path);

      if (path.toString().contains("..")) {
        return false;
      }

      return Files.exists(path) && Files.isReadable(path);
    } catch (InvalidPathException | NullPointerException exception) {
      return false;
    }
  }

  private boolean givenExtensionEqualsProbe(String fileType, String probeContentType) {
    log.debug("Comparing {} with {}", fileType, probeContentType);
    return fileType.equals(probeContentType);
  }
}
