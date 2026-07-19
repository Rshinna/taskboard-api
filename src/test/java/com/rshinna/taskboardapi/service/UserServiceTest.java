package com.rshinna.taskboardapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.dto.user.UserResponse;
import com.rshinna.taskboardapi.entity.Role;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.exception.EmailAlreadyExistsException;
import com.rshinna.taskboardapi.exception.ResourceNotFoundException;
import com.rshinna.taskboardapi.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void shouldCreateUserSuccessfully() {

    CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodrigo@email.com", "123456");

    User savedUser =
        User.builder()
            .id(UUID.randomUUID())
            .name("Rodrigo")
            .email("rodrigo@email.com")
            .password("senha-criptografada")
            .role(Role.USER)
            .build();

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

    when(passwordEncoder.encode("123456")).thenReturn("senha-criptografada");

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User result = userService.createUser(request);
    assertEquals("rodrigo@email.com", result.getEmail());
    assertEquals("senha-criptografada", result.getPassword());

    verify(userRepository).findByEmail("rodrigo@email.com");
    verify(passwordEncoder).encode("123456");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {

    CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodrigo@email.com", "123456");

    User user =
        User.builder()
            .id(UUID.randomUUID())
            .name("Rodrigo")
            .email("rodrigo@email.com")
            .password("senha-criptografada")
            .role(Role.USER)
            .build();

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

    EmailAlreadyExistsException exception =
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));

    assertEquals("Email already registered", exception.getMessage());

    verify(userRepository).findByEmail("rodrigo@email.com");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void shouldPromoteUserSuccessfully() {

    UUID userId = UUID.randomUUID();

    User user =
        User.builder()
            .id(userId)
            .name("Rodrigo")
            .email("rodrigo@email.com")
            .password("senha-criptografada")
            .role(Role.USER)
            .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    UserResponse promotedUser = userService.promoteUser(userId);

    assertEquals(Role.ADMIN, user.getRole());
    assertEquals(Role.ADMIN, promotedUser.role());

    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {

    UUID userId = UUID.randomUUID();

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> userService.promoteUser(userId));

    assertEquals("User not found", exception.getMessage());

    verify(userRepository).findById(userId);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void shouldAlwaysCreateUserWithRoleUser() {

    CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodrigo@email.com", "123456");

    when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
    when(passwordEncoder.encode("123456")).thenReturn("senha-criptografada");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = userService.createUser(request);

    assertEquals(Role.USER, result.getRole());
  }
}
