package com.task.gymmanagment.domain.dto.request;

import com.task.gymmanagment.domain.MembershipType;
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