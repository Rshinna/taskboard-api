package com.rshinna.taskboardapi.service;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(CreateUserRequest request) {

    if (userRepository.existsByEmail(request.email())) {
      throw new RuntimeException("Email already registered");
    }

    User user =
        User.builder()
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(request.role())
            .build();

    return userRepository.save(user);
  }
}
