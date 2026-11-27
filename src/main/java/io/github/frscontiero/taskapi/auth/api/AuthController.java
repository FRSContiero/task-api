package io.github.frscontiero.taskapi.auth.api;

import io.github.frscontiero.taskapi.auth.application.ChangePasswordUseCase;
import io.github.frscontiero.taskapi.auth.application.LoginUseCase;
import io.github.frscontiero.taskapi.auth.application.LogoutCommand;
import io.github.frscontiero.taskapi.auth.application.LogoutUseCase;
import io.github.frscontiero.taskapi.auth.application.RefreshTokensUseCase;
import io.github.frscontiero.taskapi.shared.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Autenticação")
public class AuthController {

  private final LoginUseCase login;
  private final RefreshTokensUseCase refreshTokens;
  private final LogoutUseCase logout;
  private final ChangePasswordUseCase changePassword;

  public AuthController(
      LoginUseCase login,
      RefreshTokensUseCase refreshTokens,
      LogoutUseCase logout,
      ChangePasswordUseCase changePassword) {
    this.login = login;
    this.refreshTokens = refreshTokens;
    this.logout = logout;
    this.changePassword = changePassword;
  }

  @PostMapping("/api/auth/login")
  @Operation(summary = "Autentica com email e senha e emite os tokens")
  @ApiResponse(responseCode = "200", description = "Autenticado")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Email ou senha incorretos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  public TokenResponse login(@Valid @RequestBody LoginRequest request) {
    return TokenResponse.from(login.execute(request.toCommand()));
  }

  @PostMapping("/api/auth/refresh")
  @Operation(summary = "Troca o refresh token por um novo par de tokens")
  @ApiResponse(responseCode = "200", description = "Tokens renovados")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Refresh token inválido, expirado ou já usado",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "409",
      description = "O mesmo refresh token foi usado em duas requisições simultâneas",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return TokenResponse.from(refreshTokens.execute(request.refreshToken()));
  }

  @PostMapping("/api/auth/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Encerra a sessão revogando o refresh token")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @ApiResponse(responseCode = "204", description = "Sessão encerrada")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado")
  public void logout(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RefreshTokenRequest request) {
    logout.execute(new LogoutCommand(userId(jwt), request.refreshToken()));
  }

  @PutMapping("/api/users/me/password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Troca a senha do usuário autenticado e encerra todas as sessões")
  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @ApiResponse(responseCode = "204", description = "Senha alterada")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado")
  @ApiResponse(
      responseCode = "422",
      description = "Senha atual incorreta",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  public void changePassword(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
    changePassword.execute(request.toCommand(userId(jwt)));
  }

  private static UUID userId(Jwt jwt) {
    return UUID.fromString(jwt.getSubject());
  }
}
