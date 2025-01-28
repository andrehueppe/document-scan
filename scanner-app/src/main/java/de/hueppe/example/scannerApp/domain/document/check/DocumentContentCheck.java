package de.hueppe.example.scannerApp.domain.document.check;

import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;

public interface DocumentContentCheck {
  String getName();
  void perform(DocumentParser documentParser);
}
