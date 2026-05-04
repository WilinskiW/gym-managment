package com.task.gymmanagment.domain;

import lombok.Builder;

@Builder
public record GymInfoDto(
        Long id,
        String name,
        String address,
        String phoneNumber
) {
}
