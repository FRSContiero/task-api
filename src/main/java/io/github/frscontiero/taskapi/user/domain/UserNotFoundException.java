package io.github.frscontiero.taskapi.user.domain;

import io.github.frscontiero.taskapi.shared.error.NotFoundException;
import java.util.UUID;

public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException(UUID id) {
    super("User %s not found".formatted(id));
  }
}
