package com.rshinna.taskboardapi.dto.task;

import com.rshinna.taskboardapi.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateTaskRequest(
    @Schema(description = "Título de tarefa", example = "Estudar todo dia") String title,
    @Schema(description = "Descrição detalhada da tarefa", example = "Atualização de dados")
        String description,
    @Schema(description = "Status de tarefa", example = "IN_PROGRESS") TaskStatus status) {}
