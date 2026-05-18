package com.rshinna.taskboardapi.repository;

import com.rshinna.taskboardapi.entity.Task;
import com.rshinna.taskboardapi.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findAllByUser(User user);

  Optional<Task> findByIdAndUser(UUID id, User user);
}
