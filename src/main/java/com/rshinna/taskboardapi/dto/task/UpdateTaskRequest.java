package com.rshinna.taskboardapi.dto.task;

import com.rshinna.taskboardapi.entity.TaskStatus;

public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatus status
) {
}
