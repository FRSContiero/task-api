package io.github.frscontiero.taskapi.user.infrastructure;

import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(Email email);
}
