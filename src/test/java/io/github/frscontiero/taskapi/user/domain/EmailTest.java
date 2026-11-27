package io.github.frscontiero.taskapi.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  void shouldNormalizeToLowercaseWithoutSurroundingSpaces() {
    Email email = new Email("  Maria.Souza@Example.COM ");

    assertThat(email.value()).isEqualTo("maria.souza@example.com");
  }

  @Test
  void shouldBeEqualRegardlessOfCase() {
    assertThat(new Email("MARIA@example.com")).isEqualTo(new Email("maria@example.com"));
  }

  @Test
  void shouldRejectBlankValue() {
    assertThatThrownBy(() -> new Email("   ")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRejectValueWithoutAtSign() {
    assertThatThrownBy(() -> new Email("maria.example.com"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
