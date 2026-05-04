package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import com.task.gymmanagment.domain.dto.response.MembershipPlanInfoResponseDto;
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

    public List<GymInfoResponseDto> getAllGyms() {
        return managmentService.findAllGyms();
    }

    public Long addMembershipToGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        return managmentService.createMembershipPlanForGym(membershipPlanRequest);
    }

    public List<MembershipPlanInfoResponseDto> getGymAllMembershipPlans(String gymName) {
        return managmentService.findGymAllMembershipPlans(gymName);
    }
}
