package de.hueppe.example.scannerApp.domain.document.check;

import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;

public interface DocumentContentCheck {
  void check(DocumentParser documentParser);
}
