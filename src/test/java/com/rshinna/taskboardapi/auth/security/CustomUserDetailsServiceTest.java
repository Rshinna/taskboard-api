package com.rshinna.taskboardapi.auth.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rshinna.taskboardapi.entity.Role;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsService customUserDetailsService;

  @Test
  void shouldLoadUserByUsernameSuccessfully() {

    User user =
        User.builder().email("rodrigo@email.com").password("123456").role(Role.USER).build();

    when(userRepository.findByEmail("rodrigo@email.com")).thenReturn(Optional.of(user));

    UserDetails result = customUserDetailsService.loadUserByUsername("rodrigo@email.com");

    assertEquals("rodrigo@email.com", result.getUsername());

    verify(userRepository).findByEmail("rodrigo@email.com");
  }

  @Test
  void shouldThrowExceptionWhenUserDoesNotExist() {

    when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

    UsernameNotFoundException exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername("inexistente@email.com"));

    assertEquals("Username does not exist or password is invalid", exception.getMessage());

    verify(userRepository).findByEmail("inexistente@email.com");
  }
}
