package de.hueppe.example.scannerApp.domain.document.filter;

import de.hueppe.example.scannerApp.domain.document.mime.MimeTypeDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.util.List;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class FilePreprocessingFilter implements DocumentPreprocessingFilter {

  public static final List<String> FILE_EXTENSION_WHITELIST = List.of("application/pdf", "application/octet-stream");
  public static final String BASE_PATH = "testData/";
  public static final String FILTER_NAME = "File preprocessing filter";

  private final MimeTypeDetector mimeTypeDetector;

  private InputStream resourceStream;

  @Override
  public String getName() {
    return FILTER_NAME;
  }

  @Override
  public void validate(String url) {
    if (!isValidFilePath(url) || !probeMimeType()) {
      throw new IllegalStateException("Invalid document mime type");
    }
  }

  private boolean probeMimeType() {
    try {
      String mimeType = mimeTypeDetector.detect(resourceStream);
      return FILE_EXTENSION_WHITELIST.contains(mimeType);
    } catch (IOException e) {
      return false;
    }
  }

  private boolean isValidFilePath(String filePath) {
    try {
      log.debug("Attempt to validate new file {}", filePath);

      //TODO: Improve checks on path traversal
      if (filePath.contains("..")) {
        throw new PathTraversalException("Path traversal detected: " + filePath);
      }

      ClassLoader classLoader = getClass().getClassLoader();
      InputStream resourceStream = classLoader.getResourceAsStream(BASE_PATH + filePath);

      if (resourceStream == null) {
        log.error("Resource not found: {}", BASE_PATH + filePath);
        return false;
      }

      try (resourceStream) {
        return true;
      }
    } catch (InvalidPathException | NullPointerException | IOException exception) {
      log.error("Pre processing filter failed with exception: {}", exception.getMessage());
      return false;
    }
  }

  public static class PathTraversalException extends RuntimeException {
    public PathTraversalException(String message) {
      super(message);
    }
  }
}
