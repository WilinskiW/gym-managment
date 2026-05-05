package com.task.gymmanagement.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddGymRequestDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @NotBlank(message = "Address cannot be empty")
        @Size(min = 2, max = 255, message = "Address must be between 2 and 255 characters")
        String address,

        @NotBlank(message = "Phone number cannot be empty")
        @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number format")
        String phoneNumber
) {
}
