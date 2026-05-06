package com.task.gymmanagement.infrastructure.error;

import lombok.Builder;

import java.util.Map;

@Builder
public record ValidationErrorResponseDto(
        int status,
        String message,
        Map<String, String> errors
) {
}
