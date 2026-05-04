package com.task.gymmanagment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymManagmentFacade {

    private final GymManagmentService managmentService;

    public Long addGym(AddGymRequestDto gymRequestDto) {
        return managmentService.createGym(gymRequestDto);
    }

    public List<GymInfoDto> getAllGyms() {
        return managmentService.findAllGyms();
    }
}
