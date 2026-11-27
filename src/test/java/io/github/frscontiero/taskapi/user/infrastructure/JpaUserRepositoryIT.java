package io.github.frscontiero.taskapi.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.TestcontainersConfiguration;
import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.EmailAlreadyRegisteredException;
import io.github.frscontiero.taskapi.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, JpaUserRepository.class})
class JpaUserRepositoryIT {

  @Autowired JpaUserRepository users;

  @Test
  void shouldPersistAndFindUserById() {
    User maria = users.save(UserFixtures.maria());

    assertThat(users.findById(maria.getId()))
        .get()
        .extracting(User::getEmail)
        .isEqualTo(new Email("maria.souza@example.com"));
  }

  @Test
  void shouldFindEmailIgnoringCase() {
    users.save(UserFixtures.maria());

    assertThat(users.existsByEmail(new Email("MARIA.SOUZA@EXAMPLE.COM"))).isTrue();
  }

  @Test
  void shouldTranslateUniqueEmailViolation() {
    users.save(UserFixtures.maria());

    assertThatThrownBy(() -> users.save(UserFixtures.maria()))
        .isInstanceOf(EmailAlreadyRegisteredException.class);
  }
}
