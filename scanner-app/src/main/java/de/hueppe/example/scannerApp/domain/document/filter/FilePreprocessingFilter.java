package de.hueppe.example.scannerApp.domain.document.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
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
public class FilePreprocessingFilter implements DocumentPreprocessingFilter {

  public static final List<String> FILE_EXTENSION_WHITELIST = List.of("application/pdf");
  private static final String BASE_PATH = "testData/";

  private File loadedFile;

  @Override
  public boolean validate(String fileType, String url) {
    return isValidFilePath(url)
        && probeMimeType(fileType);
  }

  private boolean probeMimeType(String fileType) {
    try {
      //TODO: create adapter and hide 3rd party API from here
      Tika tika = new Tika();
      String mimeType = tika.detect(loadedFile);

      return FILE_EXTENSION_WHITELIST.contains(mimeType)
          && givenExtensionEqualsProbe(fileType, mimeType);

    } catch (IOException e) {
      return false;
    }
  }

  private boolean isValidFilePath(String filePath) {
    try {
      log.debug("Attempt to validate new file {}", filePath);

      if (filePath.contains("..")) {
        return false;
      }

      ClassLoader classLoader = getClass().getClassLoader();
      loadedFile = new File(classLoader.getResource(BASE_PATH + filePath).getFile());

      return Files.exists(loadedFile.toPath()) && Files.isReadable(loadedFile.toPath());
    } catch (InvalidPathException | NullPointerException exception) {
      log.error("Pre processing filter failed with exception: {}", exception.getMessage());
      return false;
    }
  }

  private boolean givenExtensionEqualsProbe(String fileType, String probeContentType) {
    log.debug("Comparing {} with {}", fileType, probeContentType);
    return fileType.equals(probeContentType);
  }
}
