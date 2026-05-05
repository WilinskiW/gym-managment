package com.task.gymmanagment.domain.dto.request;

import lombok.Builder;

@Builder
public record AddMemberRequestDto(
        Long membershipId,
        String fullName,
        String email
) {
}
