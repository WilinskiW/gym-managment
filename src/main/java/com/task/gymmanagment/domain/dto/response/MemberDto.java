package com.task.gymmanagment.domain.dto.response;

import com.task.gymmanagment.domain.MemberStatus;
import lombok.Builder;

@Builder
public record MemberDto(
        String name,
        String membershipPlan,
        MemberStatus status
) {
}
