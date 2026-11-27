package io.github.frscontiero.taskapi.user.api;

import io.github.frscontiero.taskapi.shared.config.OpenApiConfig;
import io.github.frscontiero.taskapi.user.application.GetCurrentUserUseCase;
import io.github.frscontiero.taskapi.user.application.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Usuários")
public class UserController {

  private static final URI CURRENT_USER_URI = URI.create("/api/users/me");

  private final RegisterUserUseCase registerUser;
  private final GetCurrentUserUseCase getCurrentUser;

  public UserController(RegisterUserUseCase registerUser, GetCurrentUserUseCase getCurrentUser) {
    this.registerUser = registerUser;
    this.getCurrentUser = getCurrentUser;
  }

  @PostMapping("/api/auth/register")
  @Operation(summary = "Cadastra um usuário")
  @ApiResponse(responseCode = "201", description = "Usuário cadastrado")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "409",
      description = "Email já cadastrado",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
    UserResponse user = UserResponse.from(registerUser.execute(request.toCommand()));
    return ResponseEntity.created(CURRENT_USER_URI).body(user);
  }

  @GetMapping("/api/users/me")
  @Operation(summary = "Retorna o usuário autenticado")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @ApiResponse(responseCode = "200", description = "Usuário autenticado")
  @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado")
  @ApiResponse(
      responseCode = "404",
      description = "Usuário do token não existe mais",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
    return UserResponse.from(getCurrentUser.execute(UUID.fromString(jwt.getSubject())));
  }
}
