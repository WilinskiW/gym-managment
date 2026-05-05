package com.task.gymmanagement.domain.dto.request;

import lombok.Builder;

@Builder
public record AddMemberRequestDto(
        Long membershipId,
        String fullName,
        String email
) {
}
