package com.rshinna.taskboardapi.service;

import com.rshinna.taskboardapi.auth.service.AuthenticatedUserService;
import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import com.rshinna.taskboardapi.dto.task.UpdateTaskRequest;
import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.entity.User;
import com.rshinna.taskboardapi.exception.ResourceNotFoundException;
import com.rshinna.taskboardapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final AuthenticatedUserService authenticatedUserService;

    public Task createTask(CreateTaskRequest request){

        User user = authenticatedUserService.getAuthenticatedUser();

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .user(user)
                .build();

        return taskRepository.save(task);

    }

    public List<Task> listTasks(){

        User user = authenticatedUserService.getAuthenticatedUser();
        return taskRepository.findAllByUser(user);
    }

    public Task getTaskById(UUID id){
        User user = authenticatedUserService.getAuthenticatedUser();
        return taskRepository.findByIdAndUser(id, user).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public Task updateTask(UUID id, UpdateTaskRequest request){
        if(request.title() == null && request.description() == null && request.status() == null){
          throw new IllegalArgumentException("At least one field must be provided");
        }

        Task task = getTaskById(id);

        if(request.title() != null) task.setTitle(request.title());
        if(request.description() != null) task.setDescription(request.description());
        if(request.status() != null) task.setStatus(request.status());

        return taskRepository.save(task);
    }

    public void deleteTask(UUID id){
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
