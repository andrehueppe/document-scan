package de.hueppe.example.scannerApp.domain.document.filter;

public interface DocumentPreprocessingFilter {
  void validate(String fileType, String url);
}
