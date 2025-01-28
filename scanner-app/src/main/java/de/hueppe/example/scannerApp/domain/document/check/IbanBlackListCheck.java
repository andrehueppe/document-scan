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

  public static final String CHECK_NAME = "IBAN Black list check";
  private final IbanBlackListHolder ibanBlackListHolder;

  @Override
  public String getName() {
    return CHECK_NAME;
  }

  public void perform(DocumentParser documentParser) {
    documentParser.extractIban()
        .map(iban -> {
          if (ibanBlackListHolder.isBlacklisted(iban)) {
            throw new IllegalStateException("IBAN is blacklisted: " + iban.getValue());
          }
          return false;
        })
        .orElseThrow(() -> new IllegalStateException("Failed to extract IBAN from document"));
  }
}
