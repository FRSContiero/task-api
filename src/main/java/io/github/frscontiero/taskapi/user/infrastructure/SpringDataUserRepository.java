package io.github.frscontiero.taskapi.user.infrastructure;

import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(Email email);

  boolean existsByEmail(Email email);
}
