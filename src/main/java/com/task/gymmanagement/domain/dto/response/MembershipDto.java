package com.task.gymmanagement.domain.dto.response;

import com.task.gymmanagement.domain.MembershipType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MembershipDto(
        Long id,
        String name,
        MembershipType type,
        BigDecimal amount,
        String currency,
        int durationMonths,
        int maxMembers
) {
}
