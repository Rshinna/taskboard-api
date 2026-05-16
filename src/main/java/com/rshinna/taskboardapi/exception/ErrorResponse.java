package com.rshinna.taskboardapi.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") 
        LocalDateTime timestamp, 
        int status, 
        String error, 
        String message
        ) {}
