package de.hueppe.example.scannerApp.domain.document.iban;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryIbanBlackListProvider implements IbanBlackListProvider {

  private final Set<String> blackList = new HashSet<>();

  @Override
  public void addIban(Iban iban) {
    blackList.add(iban.getValue());
  }

  @Override
  public boolean isBlacklisted(Iban iban) {
    return blackList.contains(iban.getValue());
  }

  @Override
  public void clear() {
    blackList.clear();
  }
}
