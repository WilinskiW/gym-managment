package com.task.gymmanagement.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddMemberRequestDto(
        @NotNull(message = "Membership ID is required")
        Long membershipId,

        @NotBlank(message = "Full name cannot be empty")
        @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
        String fullName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email
) {
}
