package de.hueppe.example.scannerApp.domain.document.parser;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;

import java.util.Optional;

public interface DocumentParser {

  void init(String filePath);

  Optional<Iban> extractIban();

  void close();
}
