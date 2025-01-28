package de.hueppe.example.scannerApp.domain.document.iban;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.math.BigInteger;


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
    String sanitizedValue = value.replaceAll("\\s+", "");

    if (!isValidLength(sanitizedValue)) {
      throw new IllegalArgumentException("IBAN must be between 15 and 34 characters long: " + sanitizedValue);
    }

    if (!isValidPattern(sanitizedValue)) {
      throw new IllegalArgumentException("IBAN must start with a country code and contain only alphanumeric characters: " + sanitizedValue);
    }

    if (!isValidChecksum(sanitizedValue)) {
      throw new IllegalArgumentException("Invalid IBAN checksum: " + sanitizedValue);
    }

    this.value = sanitizedValue;
  }

  private boolean isValidLength(String iban) {
    return iban.length() >= 15 && iban.length() <= 34;
  }

  private boolean isValidPattern(String iban) {
    return iban.matches("[A-Z]{2}[0-9A-Z]{13,32}");
  }

  private boolean isValidChecksum(String iban) {
    String rearranged = iban.substring(4) + iban.substring(0, 4);
    String numericIban = rearranged.chars()
        .mapToObj(c -> Character.isLetter(c) ? String.valueOf(c - 'A' + 10) : String.valueOf((char) c))
        .reduce(String::concat)
        .orElseThrow();

    return new BigInteger(numericIban).mod(BigInteger.valueOf(97)).intValue() == 1;
  }
}
