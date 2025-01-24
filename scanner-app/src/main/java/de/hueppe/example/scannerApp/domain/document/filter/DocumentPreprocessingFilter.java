package de.hueppe.example.scannerApp.domain.document.filter;

public interface DocumentPreprocessingFilter {
  boolean validate(String fileType, String url);
}
