package io.github.frscontiero.taskapi.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import io.github.frscontiero.taskapi.TestcontainersConfiguration;
import io.github.frscontiero.taskapi.support.TestTokens;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserRegistrationIT {

  @Autowired MockMvcTester mvc;
  @Autowired JwtEncoder jwtEncoder;

  @Test
  void shouldRegisterUserAndReturnItAsAuthenticatedUser() throws Exception {
    MvcTestResult registration = register("Joana.Prado@Example.com");
    assertThat(registration).hasStatus(HttpStatus.CREATED);
    UUID id =
        UUID.fromString(JsonPath.read(registration.getResponse().getContentAsString(), "$.id"));

    var me =
        mvc.get()
            .uri("/api/users/me")
            .header(
                HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.accessTokenFor(jwtEncoder, id));

    assertThat(me)
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.email")
        .isEqualTo("joana.prado@example.com");
  }

  @Test
  void shouldRejectSameEmailWithDifferentCase() {
    register("carlos.lima@example.com");

    assertThat(register("CARLOS.LIMA@example.com")).hasStatus(HttpStatus.CONFLICT);
  }

  private MvcTestResult register(String email) {
    return mvc.post()
        .uri("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            """
            {"name": "Test User", "email": "%s", "password": "s3nha-segura"}
            """
                .formatted(email))
        .exchange();
  }
}
