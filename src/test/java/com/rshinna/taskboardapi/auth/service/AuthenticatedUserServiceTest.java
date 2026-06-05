package com.rshinna.taskboardapi.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private AuthenticatedUserService authenticatedUserService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldReturnAuthenticatedUser() {

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("rodrigo@email.com");

    SecurityContextHolder.setContext(securityContext);

    User user = User.builder().email("rodrigo@email.com").build();

    when(userRepository.findByEmail("rodrigo@email.com")).thenReturn(Optional.of(user));

    User result = authenticatedUserService.getAuthenticatedUser();

    assertEquals("rodrigo@email.com", result.getEmail());

    verify(userRepository).findByEmail("rodrigo@email.com");
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("inexistente@email.com");

    SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authenticatedUserService.getAuthenticatedUser());

    assertEquals("User not found", exception.getMessage());

    verify(userRepository).findByEmail("inexistente@email.com");
  }
}
