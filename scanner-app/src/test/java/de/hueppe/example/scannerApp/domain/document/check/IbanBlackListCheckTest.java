package de.hueppe.example.scannerApp.domain.document.check;

import de.hueppe.example.scannerApp.domain.document.iban.Iban;
import de.hueppe.example.scannerApp.domain.document.iban.IbanBlackListHolder;
import de.hueppe.example.scannerApp.domain.document.parser.DocumentParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanBlackListCheckTest {

  @Mock
  DocumentParser documentParser;

  @Mock
  IbanBlackListHolder ibanBlackListHolder;

  @InjectMocks
  IbanBlackListCheck ibanBlackListCheck;

  @Test
  void should_fail_on_missing_iban() {
    when(documentParser.extractIban()).thenReturn(Optional.empty());

    IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> {
      ibanBlackListCheck.check(documentParser);
    });
    assertThat(illegalStateException.getMessage()).isEqualTo("Failed to extract IBAN from document");
  }

  @Test
  void should_fail_on_blacklisted_iban() {
    String testIban = "DE89370400440532013000";
    when(documentParser.extractIban()).thenReturn(Optional.of(new Iban(testIban)));
    when(ibanBlackListHolder.isBlacklisted(eq(new Iban(testIban)))).thenReturn(true);

    IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> {
      ibanBlackListCheck.check(documentParser);
    });
    assertThat(illegalStateException.getMessage()).isEqualTo("IBAN is blacklisted: " + testIban);
  }

  @Test
  void should_pass() {
    String testIban = "DE89370400440532013000";
    when(documentParser.extractIban()).thenReturn(Optional.of(new Iban(testIban)));
    when(ibanBlackListHolder.isBlacklisted(eq(new Iban(testIban)))).thenReturn(false);

    try {
      ibanBlackListCheck.check(documentParser);
    } catch (Exception exception) {
      fail("Caught unexpected exception: " + exception.getMessage());
    }
  }
}