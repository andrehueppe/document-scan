package de.hueppe.example.scannerApp.domain.document.parser;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;

import java.io.IOException;
import java.util.Optional;

public interface DocumentParser {

  void init(String filePath) throws IOException;

  Optional<Iban> extractIban();

  void close() throws IOException;
}
