package com.rshinna.taskboardapi.dto.user;

import com.rshinna.taskboardapi.entity.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {}
