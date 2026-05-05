package com.task.gymmanagement.infrastructure.error;

public record ErrorResponseDto(
        int status,
        String message
) {
}
