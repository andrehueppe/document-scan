package de.hueppe.example.scannerApp.domain.document.iban;

public interface IbanBlackListHolder {
  void addIban(Iban iban);
  boolean isBlacklisted(Iban iban);
  void clear();
}
