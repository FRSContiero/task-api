package io.github.frscontiero.taskapi.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpaqueTokensTest {

  @Test
  void shouldGenerateDifferentTokensEachTime() {
    assertThat(OpaqueTokens.generate()).isNotEqualTo(OpaqueTokens.generate());
  }

  @Test
  void shouldHashToSha256Hex() {
    assertThat(OpaqueTokens.hash("abc"))
        .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
  }
}
