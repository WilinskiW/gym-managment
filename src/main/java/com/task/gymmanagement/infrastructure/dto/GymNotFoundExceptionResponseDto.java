package com.task.gymmanagement.infrastructure.dto;

public record GymNotFoundExceptionResponseDto(
        int status,
        String message
) {
}
