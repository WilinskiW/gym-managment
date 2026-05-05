package com.task.gymmanagement.domain.dto.request;

import com.task.gymmanagement.domain.MembershipType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddMembershipPlanRequestDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @NotNull(message = "Membership type is required")
        MembershipType type,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters long")
        String currency,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 month")
        Integer duration,

        @NotNull(message = "Max members is required")
        @Min(value = 1, message = "Max members must be at least 1")
        Integer maxMembers,

        @NotNull(message = "Gym ID is required")
        @Min(value = 1, message = "Gym ID must be greater than 0")
        Long gymId
) {}