package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.dto.user.CreateUserRequest;
import com.rshinna.taskboardapi.dto.user.UserResponse;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "Criar usuário", description = "Cria um novo usuário")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
    @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {

    User user = userService.createUser(request);

    return new UserResponse(
        user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }

  @Operation(summary = "Meu perfil", description = "Retorna os dados do usuário autenticado")
  @GetMapping("/me")
  @SecurityRequirement(name = "bearerAuth")
  public UserResponse me(@AuthenticationPrincipal User user) {

    return new UserResponse(
        user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }

  @Operation(summary = "Área admin", description = "Acesso restrito a usuários com role ADMIN")
  @GetMapping("/admin")
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  public String admin() {
    return "área admin";
  }

  @Operation(summary = "Promover usuário", description = "Promove um usuário para ADMIN")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Usuário promovido com sucesso"),
    @ApiResponse(responseCode = "403", description = "Acesso negado"),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/promote")
  public ResponseEntity<UserResponse> promoteUser(@PathVariable UUID id) {
    return ResponseEntity.ok(userService.promoteUser(id));
  }
}
