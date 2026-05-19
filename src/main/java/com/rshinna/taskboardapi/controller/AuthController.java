package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.auth.dto.LoginRequestDTO;
import com.rshinna.taskboardapi.auth.dto.LoginResponseDTO;
import com.rshinna.taskboardapi.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "Endpoint de autorização de login")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Validar login", description = "Valida o login do usuário")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
    @ApiResponse(responseCode = "401", description = "Email ou senhas inválidos")
  })
  @PostMapping("/login")
  public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO request) {
    return authService.login(request);
  }
}
