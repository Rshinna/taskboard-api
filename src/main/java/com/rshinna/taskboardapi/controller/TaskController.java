package com.rshinna.taskboardapi.controller;

import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import com.rshinna.taskboardapi.dto.task.TaskResponse;
import com.rshinna.taskboardapi.dto.task.UpdateTaskRequest;
import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponse createTask(@RequestBody @Valid CreateTaskRequest request) {

    Task task = taskService.createTask(request);

    return mapToResponse(task);
  }

  @GetMapping
  public List<TaskResponse> listTasks() {

    return taskService.listTasks().stream().map(this::mapToResponse).toList();
  }

  @GetMapping("/{id}")
  public TaskResponse getTaskById(@PathVariable UUID id) {

    Task task = taskService.getTaskById(id);

    return mapToResponse(task);
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(
      @PathVariable UUID id, @RequestBody @Valid UpdateTaskRequest request) {

    Task task = taskService.updateTask(id, request);

    return mapToResponse(task);
  }

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
