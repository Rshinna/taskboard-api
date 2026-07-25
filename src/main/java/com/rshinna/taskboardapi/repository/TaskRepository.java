package com.rshinna.taskboardapi.repository;

import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.entity.TaskStatus;
import com.rshinna.taskboardapi.entity.User;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {
  Page<Task> findAllByUser(User user, Pageable pageable);
  Page<Task> findAllByUserAndStatus(User user, TaskStatus status, Pageable pageable);
  Optional<Task> findByIdAndUser(UUID id, User user);
}
