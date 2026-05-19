package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import com.rshinna.taskboardapi.dto.task.TaskResponse;
import com.rshinna.taskboardapi.dto.task.UpdateTaskRequest;
import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasks", description = "Endpoints para gerenciamento de tarefas")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

  private final TaskService taskService;

  @Operation(
      summary = "Criar tarefa",
      description = "Cria uma nova tarefa para o usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Tarefa criada"),
    @ApiResponse(responseCode = "401", description = "Token inválido")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponse createTask(@RequestBody @Valid CreateTaskRequest request) {

    Task task = taskService.createTask(request);

    return mapToResponse(task);
  }

  @Operation(
      summary = "Listar tarefas",
      description = "Retorna todas as tarefas do usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
    @ApiResponse(responseCode = "401", description = "Token inválido")
  })
  @GetMapping
  public List<TaskResponse> listTasks() {

    return taskService.listTasks().stream().map(this::mapToResponse).toList();
  }

  @Operation(
      summary = "Buscar para tarefa por ID",
      description = "Retorna uma tarefa específica do usuário autenticado")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
  })
  @GetMapping("/{id}")
  public TaskResponse getTaskById(@PathVariable UUID id) {

    Task task = taskService.getTaskById(id);

    return mapToResponse(task);
  }

  @Operation(summary = "Atualizar a tarefa", description = "Atualiza os dados de uma tarefa")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
  })
  @PutMapping("/{id}")
  public TaskResponse updateTask(
      @PathVariable UUID id, @RequestBody @Valid UpdateTaskRequest request) {

    Task task = taskService.updateTask(id, request);

    return mapToResponse(task);
  }

  @Operation(summary = "Excluir tarefa", description = "Remove uma tarefa do usuário autenticado")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable UUID id) {

    taskService.deleteTask(id);
  }

  private TaskResponse mapToResponse(Task task) {

    return new TaskResponse(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getStatus(),
        task.getCreatedAt(),
        task.getUpdatedAt());
  }
}
