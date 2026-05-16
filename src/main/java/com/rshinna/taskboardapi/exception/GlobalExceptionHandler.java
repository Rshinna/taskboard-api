package com.rshinna.taskboardapi.exception;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new ErrorResponse(
                LocalDateTime.now(), 401, "Unauthorized", "Invalid email or password"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

    return ResponseEntity.badRequest()
        .body(
            Map.of(
                "timestamp",
                LocalDateTime.now(),
                "status",
                400,
                "error",
                "Validation Error",
                "message",
                "Invalid data"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneric(Exception ex) {

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            Map.of(
                "timestamp",
                LocalDateTime.now(),
                "status",
                500,
                "error",
                "Internal Server Error",
                "message",
                ex.getMessage()));
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse(LocalDateTime.now(), 409, "Conflict", ex.getMessage()));
  }
}
