package com.task.gymmanagement.domain.dto.request;

import com.task.gymmanagement.domain.MembershipType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddMembershipPlanRequestDto(
        String name,
        MembershipType type,
        BigDecimal amount,
        String currency,
        int duration,
        int maxMembers,
        String gymName
) {}