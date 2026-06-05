package com.rshinna.taskboardapi.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rshinna.taskboardapi.auth.dto.LoginRequestDTO;
import com.rshinna.taskboardapi.auth.dto.LoginResponseDTO;
import com.rshinna.taskboardapi.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private AuthenticationManager authenticationManager;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthService authService;

  @Test
  void shouldLoginSuccessfully() {

    LoginRequestDTO request = new LoginRequestDTO("rodrigo@email.com", "123456");

    when(jwtService.generateToken(request.email())).thenReturn("fake-jwt-token");

    LoginResponseDTO response = authService.login(request);

    assertEquals("fake-jwt-token", response.token());

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService).generateToken(request.email());
  }

  @Test
  void shouldThrowExceptionWhenCredentialsAreInvalid() {

    LoginRequestDTO request = new LoginRequestDTO("rodrigo@email.com", "senha-errada");

    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    assertThrows(BadCredentialsException.class, () -> authService.login(request));

    verify(jwtService, never()).generateToken(any());
  }
}
