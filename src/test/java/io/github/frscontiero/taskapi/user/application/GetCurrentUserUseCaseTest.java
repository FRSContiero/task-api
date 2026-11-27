package io.github.frscontiero.taskapi.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetCurrentUserUseCaseTest {

  private final InMemoryUserRepository users = new InMemoryUserRepository();
  private final GetCurrentUserUseCase getCurrentUser = new GetCurrentUserUseCase(users);

  @Test
  void shouldReturnUserWhenItExists() {
    User maria = users.save(UserFixtures.maria());

    assertThat(getCurrentUser.execute(maria.getId())).isEqualTo(maria);
  }

  @Test
  void shouldThrowWhenUserDoesNotExist() {
    assertThatThrownBy(() -> getCurrentUser.execute(UUID.randomUUID()))
        .isInstanceOf(UserNotFoundException.class);
  }
}
