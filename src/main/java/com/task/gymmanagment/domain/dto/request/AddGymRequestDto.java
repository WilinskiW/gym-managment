package com.task.gymmanagment.domain.dto.request;

import lombok.Builder;

@Builder
public record AddGymRequestDto(
        String name,
        String address,
        String phoneNumber
) {
}
