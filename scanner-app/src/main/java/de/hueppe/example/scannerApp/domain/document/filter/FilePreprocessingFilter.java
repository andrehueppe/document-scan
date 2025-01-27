package de.hueppe.example.scannerApp.domain.document.filter;

import de.hueppe.example.scannerApp.domain.document.mime.MimeTypeDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.List;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class FilePreprocessingFilter implements DocumentPreprocessingFilter {

  public static final List<String> FILE_EXTENSION_WHITELIST = List.of("application/pdf");
  public static final String BASE_PATH = "testData/";

  private final MimeTypeDetector mimeTypeDetector;

  private File loadedFile;

  @Override
  public void validate(String fileType, String url) {
    if (!isValidFilePath(url) || !probeMimeType(fileType)) {
      throw new IllegalStateException("Invalid document mime type");
    }
  }

  private boolean probeMimeType(String fileType) {
    try {
      String mimeType = mimeTypeDetector.detect(loadedFile);
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
      loadedFile = new File(classLoader.getResource(BASE_PATH + filePath).getFile());

      return Files.exists(loadedFile.toPath()) && Files.isReadable(loadedFile.toPath());
    } catch (InvalidPathException | NullPointerException exception) {
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
