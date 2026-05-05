package com.task.gymmanagement.domain.dto.response;

import com.task.gymmanagement.domain.MemberStatus;
import lombok.Builder;

@Builder
public record MemberDto(
        Long id,
        String name,
        String membershipPlan,
        MemberStatus status
) {
}
