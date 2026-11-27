package io.github.frscontiero.taskapi.user.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import io.github.frscontiero.taskapi.shared.config.SecurityConfig;
import io.github.frscontiero.taskapi.support.TestTokens;
import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.application.GetCurrentUserUseCase;
import io.github.frscontiero.taskapi.user.application.RegisterUserCommand;
import io.github.frscontiero.taskapi.user.application.RegisterUserUseCase;
import io.github.frscontiero.taskapi.user.domain.EmailAlreadyRegisteredException;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  private static final String VALID_REQUEST =
      """
      {"name": "Maria Souza", "email": "maria.souza@example.com", "password": "s3nha-segura"}
      """;

  @Autowired MockMvcTester mvc;
  @Autowired JwtEncoder jwtEncoder;

  @MockitoBean RegisterUserUseCase registerUser;
  @MockitoBean GetCurrentUserUseCase getCurrentUser;

  @Test
  void shouldReturn201WithLocationWhenUserIsRegistered() {
    given(registerUser.execute(any(RegisterUserCommand.class))).willReturn(UserFixtures.maria());

    var response = mvc.post().uri("/api/auth/register").contentType(MediaType.APPLICATION_JSON);

    assertThat(response.content(VALID_REQUEST))
        .hasStatus(HttpStatus.CREATED)
        .hasHeader(HttpHeaders.LOCATION, "/api/users/me")
        .bodyJson()
        .extractingPath("$.email")
        .isEqualTo("maria.souza@example.com");
  }

  @Test
  void shouldNotExposePasswordHash() {
    given(registerUser.execute(any(RegisterUserCommand.class))).willReturn(UserFixtures.maria());

    var response = mvc.post().uri("/api/auth/register").contentType(MediaType.APPLICATION_JSON);

    assertThat(response.content(VALID_REQUEST)).bodyJson().doesNotHavePath("$.passwordHash");
  }

  @Test
  void shouldReturn400WhenPasswordIsTooShort() {
    var response = mvc.post().uri("/api/auth/register").contentType(MediaType.APPLICATION_JSON);

    assertThat(
            response.content(
                """
                {"name": "Maria Souza", "email": "maria.souza@example.com", "password": "short"}
                """))
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldReturn400WhenEmailIsInvalid() {
    var response = mvc.post().uri("/api/auth/register").contentType(MediaType.APPLICATION_JSON);

    assertThat(
            response.content(
                """
                {"name": "Maria Souza", "email": "not-an-email", "password": "s3nha-segura"}
                """))
        .hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldReturn409WhenEmailIsAlreadyRegistered() {
    given(registerUser.execute(any(RegisterUserCommand.class)))
        .willThrow(new EmailAlreadyRegisteredException());

    var response = mvc.post().uri("/api/auth/register").contentType(MediaType.APPLICATION_JSON);

    assertThat(response.content(VALID_REQUEST))
        .hasStatus(HttpStatus.CONFLICT)
        .bodyJson()
        .extractingPath("$.detail")
        .isEqualTo("Email is already registered");
  }

  @Test
  void shouldReturnAuthenticatedUser() {
    User maria = UserFixtures.maria();
    given(getCurrentUser.execute(maria.getId())).willReturn(maria);

    var response = mvc.get().uri("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(maria));

    assertThat(response)
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.id")
        .isEqualTo(maria.getId().toString());
  }

  @Test
  void shouldReturn401WhenTokenIsMissing() {
    assertThat(mvc.get().uri("/api/users/me")).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenTokenIsInvalid() {
    var response =
        mvc.get().uri("/api/users/me").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt");

    assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturn404WhenTokenUserNoLongerExists() {
    User maria = UserFixtures.maria();
    given(getCurrentUser.execute(maria.getId()))
        .willThrow(new UserNotFoundException(maria.getId()));

    var response = mvc.get().uri("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(maria));

    assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
  }

  private String bearer(User user) {
    return "Bearer " + TestTokens.accessTokenFor(jwtEncoder, user.getId());
  }
}
