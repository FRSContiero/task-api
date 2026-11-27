package io.github.frscontiero.taskapi.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import io.github.frscontiero.taskapi.TestcontainersConfiguration;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/** Login, refresh, reuse detection, logout and password change against a real database. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthFlowIT {

  private static final String PASSWORD = "s3nha-segura";

  @Autowired MockMvcTester mvc;

  private String email;

  @BeforeEach
  void registerUser() {
    // A unique email per test keeps tests independent while sharing the container
    email = "user-" + System.nanoTime() + "@example.com";
    var registration =
        post(
            "/api/auth/register",
            """
            {"name": "Test User", "email": "%s", "password": "%s"}
            """
                .formatted(email, PASSWORD));
    assertThat(registration).hasStatus(HttpStatus.CREATED);
  }

  @Test
  void shouldAccessProtectedEndpointWithTokenFromLogin() throws Exception {
    MvcTestResult tokens = login(PASSWORD);

    var me =
        mvc.get()
            .uri("/api/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + field(tokens, "$.accessToken"));

    assertThat(me).hasStatusOk().bodyJson().extractingPath("$.email").isEqualTo(email);
  }

  @Test
  void shouldRejectWrongPassword() {
    assertThat(login("wrong-password")).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldRotateRefreshTokenAndRevokeEverythingOnReuse() throws Exception {
    String first = field(login(PASSWORD), "$.refreshToken");
    MvcTestResult rotated = refresh(first);
    assertThat(rotated).hasStatusOk();
    String second = field(rotated, "$.refreshToken");

    assertThat(refresh(first)).hasStatus(HttpStatus.UNAUTHORIZED);
    assertThat(refresh(second)).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldRevokeRefreshTokenOnLogout() throws Exception {
    MvcTestResult tokens = login(PASSWORD);
    String refreshToken = field(tokens, "$.refreshToken");

    var logout =
        post("/api/auth/logout", refreshBody(refreshToken))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + field(tokens, "$.accessToken"));

    assertThat(logout).hasStatus(HttpStatus.NO_CONTENT);
    assertThat(refresh(refreshToken)).hasStatus(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldEndSessionsAndAcceptNewPasswordAfterChange() throws Exception {
    MvcTestResult tokens = login(PASSWORD);

    var change =
        mvc.put()
            .uri("/api/users/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + field(tokens, "$.accessToken"))
            .content(
                """
                {"currentPassword": "%s", "newPassword": "n0va-s3nha-segura"}
                """
                    .formatted(PASSWORD));

    assertThat(change).hasStatus(HttpStatus.NO_CONTENT);
    assertThat(refresh(field(tokens, "$.refreshToken"))).hasStatus(HttpStatus.UNAUTHORIZED);
    assertThat(login(PASSWORD)).hasStatus(HttpStatus.UNAUTHORIZED);
    assertThat(login("n0va-s3nha-segura")).hasStatusOk();
  }

  private MvcTestResult login(String password) {
    return post(
            "/api/auth/login",
            """
            {"email": "%s", "password": "%s"}
            """
                .formatted(email, password))
        .exchange();
  }

  private MvcTestResult refresh(String refreshToken) {
    return post("/api/auth/refresh", refreshBody(refreshToken)).exchange();
  }

  private MockMvcTester.MockMvcRequestBuilder post(String uri, String body) {
    return mvc.post().uri(uri).contentType(MediaType.APPLICATION_JSON).content(body);
  }

  private static String refreshBody(String refreshToken) {
    return """
    {"refreshToken": "%s"}
    """
        .formatted(refreshToken);
  }

  private static String field(MvcTestResult result, String path)
      throws UnsupportedEncodingException {
    return JsonPath.read(result.getResponse().getContentAsString(), path);
  }
}
