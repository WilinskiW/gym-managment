package com.task.gymmanagement.domain.dto.request;

import lombok.Builder;

@Builder
public record AddGymRequestDto(
        String name,
        String address,
        String phoneNumber
) {
}
