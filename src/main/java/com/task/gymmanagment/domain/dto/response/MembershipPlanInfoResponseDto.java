package com.task.gymmanagment.domain.dto.response;

import com.task.gymmanagment.domain.MembershipType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MembershipPlanInfoResponseDto(
        Long id,
        String name,
        MembershipType type,
        BigDecimal amount,
        String currency,
        int durationMonths,
        int maxMembers
) {
}
