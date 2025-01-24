package de.hueppe.example.scannerApp.domain.document.iban;

public interface IbanBlackListProvider {
  void addIban(String iban);
  boolean isBlacklisted(String iban);
}
