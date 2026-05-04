package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseto;
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

    public List<GymInfoResponseto> getAllGyms() {
        return managmentService.findAllGyms();
    }
}
