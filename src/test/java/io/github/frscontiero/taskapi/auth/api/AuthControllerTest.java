package io.github.frscontiero.taskapi.auth.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import io.github.frscontiero.taskapi.auth.application.ChangePasswordCommand;
import io.github.frscontiero.taskapi.auth.application.ChangePasswordUseCase;
import io.github.frscontiero.taskapi.auth.application.LoginCommand;
import io.github.frscontiero.taskapi.auth.application.LoginUseCase;
import io.github.frscontiero.taskapi.auth.application.LogoutCommand;
import io.github.frscontiero.taskapi.auth.application.LogoutUseCase;
import io.github.frscontiero.taskapi.auth.application.RefreshTokensUseCase;
import io.github.frscontiero.taskapi.auth.application.TokenPair;
import io.github.frscontiero.taskapi.auth.domain.AccessToken;
import io.github.frscontiero.taskapi.auth.domain.IncorrectCurrentPasswordException;
import io.github.frscontiero.taskapi.auth.domain.InvalidCredentialsException;
import io.github.frscontiero.taskapi.auth.domain.InvalidRefreshTokenException;
import io.github.frscontiero.taskapi.shared.config.SecurityConfig;
import io.github.frscontiero.taskapi.support.TestTokens;
import java.time.Duration;
import java.util.UUID;
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

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

  private static final TokenPair TOKENS =
      new TokenPair(new AccessToken("signed.jwt.value", Duration.ofMinutes(15)), "refresh-token");

  private static final String LOGIN_REQUEST =
      """
      {"email": "maria.souza@example.com", "password": "s3nha-segura"}
      """;

  private static final String REFRESH_REQUEST =
      """
      {"refreshToken": "refresh-token"}
      """;

  private static final String CHANGE_PASSWORD_REQUEST =
      """
      {"currentPassword": "s3nha-segura", "newPassword": "n0va-s3nha-segura"}
      """;

  private final UUID userId = UUID.randomUUID();

  @Autowired MockMvcTester mvc;
  @Autowired JwtEncoder jwtEncoder;

  @MockitoBean LoginUseCase login;
  @MockitoBean RefreshTokensUseCase refreshTokens;
  @MockitoBean LogoutUseCase logout;
  @MockitoBean ChangePasswordUseCase changePassword;

  @Test
  void shouldReturnTokensOnLogin() {
    given(login.execute(any(LoginCommand.class))).willReturn(TOKENS);

    var response = post("/api/auth/login", LOGIN_REQUEST);

    assertThat(response)
        .hasStatusOk()
        .bodyJson()
        .isLenientlyEqualTo(
            """
            {"accessToken": "signed.jwt.value", "tokenType": "Bearer",
             "expiresIn": 900, "refreshToken": "refresh-token"}
            """);
  }

  @Test
  void shouldReturn401OnInvalidCredentials() {
    given(login.execute(any(LoginCommand.class))).willThrow(new InvalidCredentialsException());

    var response = post("/api/auth/login", LOGIN_REQUEST);

    assertThat(response)
        .hasStatus(HttpStatus.UNAUTHORIZED)
        .bodyJson()
        .extractingPath("$.detail")
        .isEqualTo("Invalid email or password");
  }

  @Test
  void shouldReturn400WhenLoginHasNoPassword() {
    var response =
        post(
            "/api/auth/login",
            """
            {"email": "maria.souza@example.com"}
            """);

    assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldReturnNewTokensOnRefresh() {
    given(refreshTokens.execute("refresh-token")).willReturn(TOKENS);

    var response = post("/api/auth/refresh", REFRESH_REQUEST);

    assertThat(response)
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.refreshToken")
        .isEqualTo("refresh-token");
  }

  @Test
  void shouldReturn401OnInvalidRefreshToken() {
    given(refreshTokens.execute("refresh-token")).willThrow(new InvalidRefreshTokenException());

    assertThat(post("/api/auth/refresh", REFRESH_REQUEST)).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturn204OnLogoutAndRevokeGivenToken() {
    var response = authenticatedPost("/api/auth/logout", REFRESH_REQUEST);

    assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
    verify(logout).execute(new LogoutCommand(userId, "refresh-token"));
  }

  @Test
  void shouldReturn401OnLogoutWithoutAccessToken() {
    assertThat(post("/api/auth/logout", REFRESH_REQUEST)).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturn204WhenPasswordIsChanged() {
    var response = authenticatedPut("/api/users/me/password", CHANGE_PASSWORD_REQUEST);

    assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
    verify(changePassword)
        .execute(new ChangePasswordCommand(userId, "s3nha-segura", "n0va-s3nha-segura"));
  }

  @Test
  void shouldReturn422WhenCurrentPasswordIsIncorrect() {
    willThrow(new IncorrectCurrentPasswordException())
        .given(changePassword)
        .execute(any(ChangePasswordCommand.class));

    var response = authenticatedPut("/api/users/me/password", CHANGE_PASSWORD_REQUEST);

    assertThat(response).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
  }

  @Test
  void shouldReturn400WhenNewPasswordIsTooShort() {
    var response =
        authenticatedPut(
            "/api/users/me/password",
            """
            {"currentPassword": "s3nha-segura", "newPassword": "short"}
            """);

    assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
  }

  private MockMvcTester.MockMvcRequestBuilder post(String uri, String body) {
    return mvc.post().uri(uri).contentType(MediaType.APPLICATION_JSON).content(body);
  }

  private MockMvcTester.MockMvcRequestBuilder authenticatedPost(String uri, String body) {
    return post(uri, body).header(HttpHeaders.AUTHORIZATION, bearer());
  }

  private MockMvcTester.MockMvcRequestBuilder authenticatedPut(String uri, String body) {
    return mvc.put()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body)
        .header(HttpHeaders.AUTHORIZATION, bearer());
  }

  private String bearer() {
    return "Bearer " + TestTokens.accessTokenFor(jwtEncoder, userId);
  }
}
