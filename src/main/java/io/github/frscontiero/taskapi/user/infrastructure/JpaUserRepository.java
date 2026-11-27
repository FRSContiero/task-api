package io.github.frscontiero.taskapi.user.infrastructure;

import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.EmailAlreadyRegisteredException;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
class JpaUserRepository implements UserRepository {

  private static final String UNIQUE_EMAIL_CONSTRAINT = "uk_users_email";

  private final SpringDataUserRepository jpa;

  JpaUserRepository(SpringDataUserRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public User save(User user) {
    try {
      // Flush now so a concurrent duplicate email surfaces here, not at commit time
      return jpa.saveAndFlush(user);
    } catch (DataIntegrityViolationException exception) {
      if (violates(exception, UNIQUE_EMAIL_CONSTRAINT)) {
        throw new EmailAlreadyRegisteredException();
      }
      throw exception;
    }
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpa.findById(id);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return jpa.existsByEmail(email);
  }

  private static boolean violates(DataIntegrityViolationException exception, String constraint) {
    return exception.getCause() instanceof ConstraintViolationException violation
        && constraint.equalsIgnoreCase(violation.getConstraintName());
  }
}
