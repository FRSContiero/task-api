package io.github.frscontiero.taskapi.shared.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Maps exceptions to ProblemDetail (RFC 9457). Validation errors (400) are already handled by
 * ResponseEntityExceptionHandler. Domain exceptions extend one of the bases in this package, so
 * this handler does not change when a new exception is added.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // 422 via valueOf: the HttpStatus constant name changed between Spring versions
  private static final HttpStatusCode UNPROCESSABLE = HttpStatusCode.valueOf(422);

  @ExceptionHandler(NotFoundException.class)
  ProblemDetail handleNotFound(NotFoundException exception) {
    return problem(HttpStatus.NOT_FOUND, "Resource not found", exception);
  }

  @ExceptionHandler(ConflictException.class)
  ProblemDetail handleConflict(ConflictException exception) {
    return problem(HttpStatus.CONFLICT, "Conflict", exception);
  }

  @ExceptionHandler(BusinessRuleException.class)
  ProblemDetail handleBusinessRule(BusinessRuleException exception) {
    return problem(UNPROCESSABLE, "Business rule violated", exception);
  }

  private static ProblemDetail problem(
      HttpStatusCode status, String title, RuntimeException exception) {
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
    detail.setTitle(title);
    return detail;
  }
}
