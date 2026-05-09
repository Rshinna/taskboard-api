package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid CreateUserRequest request){
        return userService.createUser(request);
    }

}
