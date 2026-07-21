package com.rshinna.taskboardapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rshinna.taskboardapi.auth.service.AuthenticatedUserService;
import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import com.rshinna.taskboardapi.dto.task.UpdateTaskRequest;
import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.entity.TaskStatus;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.exception.ResourceNotFoundException;
import com.rshinna.taskboardapi.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock private TaskRepository taskRepository;

  @Mock private AuthenticatedUserService authenticatedUserService;

  @InjectMocks private TaskService taskService;

  @Test
  void shouldCreateTaskSuccessfully() {

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@test.com").build();

    CreateTaskRequest request =
        new CreateTaskRequest("Estudar Mockito", "Aprender testes unitários");

    Task savedTask =
        Task.builder().title(request.title()).description(request.description()).user(user).build();

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    Task result = taskService.createTask(request);

    assertEquals("Estudar Mockito", result.getTitle());
    assertEquals("Aprender testes unitários", result.getDescription());

    verify(authenticatedUserService).getAuthenticatedUser();
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  void shouldListTasksSuccessfully() {

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@test.com").build();

    Task task1 =
        Task.builder().title("Estudar Mockito").description("Aprender mocks").user(user).build();
    Task task2 =
        Task.builder().title("Estudar Junit").description("Aprender asserts").user(user).build();

    List<Task> tasks = List.of(task1, task2);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Task> taskPage = new PageImpl<>(tasks, pageable, tasks.size());

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findAllByUser(user, pageable)).thenReturn(taskPage);

    Page<Task> result = taskService.listTasks(pageable);

    assertEquals(2, result.getContent().size());
    assertEquals("Estudar Mockito", result.getContent().get(0).getTitle());
    assertEquals("Estudar Junit", result.getContent().get(1).getTitle());

    verify(authenticatedUserService).getAuthenticatedUser();
    verify(taskRepository).findAllByUser(user,pageable);
  }

  @Test
  void shouldGetTaskByIdSuccessfully() {

    UUID taskId = UUID.randomUUID();

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();

    Task task =
        Task.builder().title("Estudar Mockito").description("Aprender testes").user(user).build();
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));

    Task result = taskService.getTaskById(taskId);

    assertEquals(task, result);

    verify(authenticatedUserService).getAuthenticatedUser();
    verify(taskRepository).findByIdAndUser(taskId, user);
  }

  @Test
  void shouldThrowExceptionWhenTaskNotFound() {

    UUID taskId = UUID.randomUUID();

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(taskId));

    assertEquals("Task not found", exception.getMessage());

    verify(authenticatedUserService).getAuthenticatedUser();
    verify(taskRepository).findByIdAndUser(taskId, user);
  }

  @Test
  void shouldUpdateTaskIsSuccessfully() {

    UUID taskId = UUID.randomUUID();

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();
    Task task =
        Task.builder().title("Titulo antigo").description("Descrição antiga").user(user).build();
    UpdateTaskRequest request =
        new UpdateTaskRequest("Título novo", "Descrição nova", TaskStatus.IN_PROGRESS);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    Task result = taskService.updateTask(taskId, request);

    assertEquals("Título novo", result.getTitle());
    assertEquals("Descrição nova", result.getDescription());
    assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

    verify(taskRepository).save(task);
  }

  @Test
  void shouldThrowExceptionWhenNoFieldIsProvided() {

    UUID taskId = UUID.randomUUID();

    UpdateTaskRequest request = new UpdateTaskRequest(null, null, null);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, request));
    assertEquals("At least one field must be provided", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNoneExistentTask() {

    UUID taskId = UUID.randomUUID();

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();

    UpdateTaskRequest request = new UpdateTaskRequest("Novo título", null, null);

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, request));
  }

  @Test
  void shouldDeleteTaskIsSuccessfully() {

    UUID taskId = UUID.randomUUID();

    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();
    Task task = Task.builder().title("Task").description("Descrição").user(user).build();

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));

    taskService.deleteTask(taskId);

    verify(taskRepository).delete(task);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNoneExistentTask() {

    UUID taskId = UUID.randomUUID();
    User user = User.builder().id(UUID.randomUUID()).email("rodrigo@email.com").build();

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
    when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(taskId));

    verify(taskRepository, never()).delete(any());
  }
}
