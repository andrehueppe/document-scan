package de.hueppe.example.scannerApp.domain.document.iban;

public interface IbanBlackListProvider {
  void addIban(Iban iban);
  boolean isBlacklisted(Iban iban);
  void clear();
}
