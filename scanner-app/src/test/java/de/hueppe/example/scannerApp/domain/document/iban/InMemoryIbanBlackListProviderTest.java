package de.hueppe.example.scannerApp.domain.document.iban;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryIbanBlackListProviderTest {

  private final InMemoryIbanBlackListProvider blackListProvider = new InMemoryIbanBlackListProvider();

  static Stream<Arguments> provideValidIbanTestData() {
    return Stream.of(
        Arguments.of("DE89370400440532013000", true),
        Arguments.of("FR1420041010050500013M02606", false),
        Arguments.of("ES9121000418450200051332", false)
    );
  }

  static Stream<Arguments> provideInvalidIbanTestData() {
    return Stream.of(
        Arguments.of("INVALIDIBAN123"),
        Arguments.of("SHORT"),
        Arguments.of("")
    );
  }

  @BeforeEach
  void setUp() {
    blackListProvider.clear();
    blackListProvider.addIban(new Iban("DE89370400440532013000"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidIbanTestData")
  void should_fail_validation(String iban) {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> blackListProvider.isBlacklisted(new Iban(iban)));

    assertThat(illegalArgumentException.getMessage())
        .isEqualTo("Invalid IBAN checksum: " + iban);
  }

  @ParameterizedTest
  @MethodSource("provideValidIbanTestData")
  void should_match_blackListed_iban(String iban, boolean expected) {
    assertThat(blackListProvider.isBlacklisted(new Iban(iban))).isEqualTo(expected);
  }
}