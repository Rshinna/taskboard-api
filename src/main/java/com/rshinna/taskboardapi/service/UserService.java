package com.rshinna.taskboardapi.service;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.dto.user.UserResponse;
import com.rshinna.taskboardapi.entity.Role;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.exception.EmailAlreadyExistsException;
import com.rshinna.taskboardapi.exception.ResourceNotFoundException;
import com.rshinna.taskboardapi.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(CreateUserRequest request) {

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EmailAlreadyExistsException("Email already registered");
    }

    User user =
        User.builder()
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build();

    return userRepository.save(user);
  }

  public UserResponse promoteUser(UUID id) {

    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.setRole(Role.ADMIN);
    userRepository.save(user);
    return mapToResponse(user);
  }

  private UserResponse mapToResponse(User user) {
    return new UserResponse(
        user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }
}
