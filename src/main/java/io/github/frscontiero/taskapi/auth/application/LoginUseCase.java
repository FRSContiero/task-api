package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.InvalidCredentialsException;
import io.github.frscontiero.taskapi.user.domain.PasswordHasher;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUseCase {

  private final UserRepository users;
  private final PasswordHasher passwordHasher;
  private final TokenPairIssuer tokenPairs;

  LoginUseCase(UserRepository users, PasswordHasher passwordHasher, TokenPairIssuer tokenPairs) {
    this.users = users;
    this.passwordHasher = passwordHasher;
    this.tokenPairs = tokenPairs;
  }

  @Transactional
  public TokenPair execute(LoginCommand command) {
    Optional<User> user = users.findByEmail(command.email());
    if (user.isEmpty()) {
      // Spends the same BCrypt time as a wrong password, so timing does not reveal the email
      passwordHasher.hash(command.password());
      throw new InvalidCredentialsException();
    }
    if (!passwordHasher.matches(command.password(), user.get().getPasswordHash())) {
      throw new InvalidCredentialsException();
    }
    return tokenPairs.issueFor(user.get().getId());
  }
}
