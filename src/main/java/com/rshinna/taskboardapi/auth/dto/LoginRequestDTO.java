package com.rshinna.taskboardapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @Schema(description = "Email do usuário", example = "joao@email.com") @Email @NotBlank
        String email,
    @Schema(description = "Senha do usuário", example = "senha123") @NotBlank String password) {}
