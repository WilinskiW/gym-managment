package com.task.gymmanagement.infrastructure.error;

import java.util.Map;

public record ValidationErrorResponseDto(
        int status,
        String message,
        Map<String, String> errors
) {
}
