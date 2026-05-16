package com.rshinna.taskboardapi.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rshinna.taskboardapi.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    ErrorResponse error =
        new ErrorResponse(LocalDateTime.now(), 401, "Unauthorized", "Invalid or missing token");

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    response
        .getWriter()
        .write(new ObjectMapper().findAndRegisterModules().writeValueAsString(error));
  }
}
