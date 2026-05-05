package com.task.gymmanagment.domain.dto.response;

import lombok.Builder;

@Builder
public record GymDto(
        Long id,
        String name,
        String address,
        String phoneNumber
) {
}
