package com.rshinna.taskboardapi.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

        @Schema(description = "Título da tarefa", example = "Estudar Spring Security")
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Finalizar autenticação JWT")
        String description
) {
}
