package com.task.gymmanagement.domain.dto.request;

import com.task.gymmanagement.domain.MembershipType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Builder
public record AddMembershipPlanRequestDto(
        @NotBlank
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @NotNull
        MembershipType type,

        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank
        @Range(min = 3, max = 3, message = "Currency must be 3 characters long")
        String currency,

        @NotNull
        @Min(1)
        Integer duration,

        @NotNull
        @Min(1)
        Integer maxMembers,

        @NotBlank
        String gymName
) {}