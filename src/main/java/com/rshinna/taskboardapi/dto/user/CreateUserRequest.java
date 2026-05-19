package com.rshinna.taskboardapi.dto.user;

import com.rshinna.taskboardapi.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @Schema(description = "Nome do usuário", example = "João Silva")
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,
    @Schema(description = "Email do usuário", example = "joao@email.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,
    @Schema(description = "Senha do usuário", example = "senha123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must contain at least 6 characters")
        String password,
    @Schema(description = "Role de usuário", example = "USER") Role role) {}
