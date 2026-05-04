package com.task.gymmanagment.domain;

import lombok.Builder;

@Builder
public record AddGymRequestDto(
        String name,
        String address,
        String phoneNumber
) {
}
