package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.dto.user.UserResponse;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {

    User user = userService.createUser(request);

    return new UserResponse(
        user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }

  @GetMapping("/me")
  public UserResponse me(@AuthenticationPrincipal User user){

    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public String admin(){
      return "área admin";
  }
}
