package com.task.gymmanagement.infrastructure.dto;

public record ValidationErrorResponseDto(
        int status,
        String message
) {
}
