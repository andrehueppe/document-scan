package de.hueppe.example.scannerApp.domain.document.check;

import de.hueppe.example.scannerApp.domain.document.iban.IbanBlackListHolder;
import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@RequiredArgsConstructor
public class IbanBlackListCheck implements DocumentContentCheck {

  private final IbanBlackListHolder ibanBlackListHolder;

  public void check(DocumentParser documentParser) {
    documentParser.extractIban()
        .map(iban -> {
          if (ibanBlackListHolder.isBlacklisted(iban)) {
            throw new IllegalStateException("IBAN is blacklisted: " + iban);
          }
          return false;
        })
        .orElseThrow(() -> new IllegalStateException("Failed to extract IBAN from document"));
  }
}
