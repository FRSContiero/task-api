package io.github.frscontiero.taskapi.user.application;

import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository implements UserRepository {

  private final Map<UUID, User> users = new HashMap<>();

  @Override
  public User save(User user) {
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
  }

  @Override
  public boolean existsByEmail(Email email) {
    return findByEmail(email).isPresent();
  }
}
