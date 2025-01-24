package de.hueppe.example.scannerApp.domain.document.iban;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryIbanBlackListProvider implements IbanBlackListProvider {

  private final Set<String> blackList = new HashSet<>();

  @Override
  public void addIban(String iban) {
    blackList.add(iban);
  }

  @Override
  public boolean isBlacklisted(String iban) {
    return blackList.contains(iban);
  }
}
