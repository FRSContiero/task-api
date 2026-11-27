package io.github.frscontiero.taskapi.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  /**
   * @throws EmailAlreadyRegisteredException if another user took the email concurrently
   */
  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(Email email);

  boolean existsByEmail(Email email);
}
