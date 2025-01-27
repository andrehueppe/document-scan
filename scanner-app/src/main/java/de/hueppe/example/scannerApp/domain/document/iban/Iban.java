package de.hueppe.example.scannerApp.domain.document.iban;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class Iban {

  @NotNull(message = "IBAN cannot be null")
  @Size(min = 15, max = 34, message = "IBAN must be between 15 and 34 characters long")
  @Pattern(
      regexp = "[A-Z]{2}[0-9A-Z]{13,32}",
      message = "IBAN must start with a country code and contain only alphanumeric characters"
  )
  String value;

  public Iban(String value) {
    this.value = value;

    if (!isValidChecksum(value)) {
      throw new IllegalArgumentException("Invalid IBAN checksum: " + value);
    }
  }

  //TODO: consider using external/official lib for validation an IBAN
  private boolean isValidChecksum(String iban) {
    if (iban.length() < 15) {
      return false;
    }

    String rearranged = iban.substring(4) + iban.substring(0, 4);
    String numericIban = rearranged.chars()
        .mapToObj(c -> Character.isLetter(c) ? String.valueOf(c - 'A' + 10) : String.valueOf((char) c))
        .reduce(String::concat)
        .orElseThrow();

    return new java.math.BigInteger(numericIban).mod(java.math.BigInteger.valueOf(97)).intValue() == 1;
  }
}
