package com.rshinna.taskboardapi.dto.task;

import com.rshinna.taskboardapi.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
