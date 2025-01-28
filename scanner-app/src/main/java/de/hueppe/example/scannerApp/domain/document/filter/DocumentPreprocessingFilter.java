package de.hueppe.example.scannerApp.domain.document.filter;

public interface DocumentPreprocessingFilter {
  String getName();
  void validate(String url);
}
