package io.github.frscontiero.taskapi.user.application;

import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserNotFoundException;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCurrentUserUseCase {

  private final UserRepository users;

  public GetCurrentUserUseCase(UserRepository users) {
    this.users = users;
  }

  @Transactional(readOnly = true)
  public User execute(UUID userId) {
    return users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }
}
