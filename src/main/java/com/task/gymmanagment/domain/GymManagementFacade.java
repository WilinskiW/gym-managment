package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymDto;
import com.task.gymmanagment.domain.dto.response.MemberDto;
import com.task.gymmanagment.domain.dto.response.MembershipDto;
import com.task.gymmanagment.domain.dto.response.RevenueReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymManagementFacade {

    private final GymManagementService managmentService;

    public Long addGym(AddGymRequestDto gymRequestDto) {
        return managmentService.createGym(gymRequestDto);
    }

    public List<GymDto> getAllGyms() {
        return managmentService.findAllGyms();
    }

    public Long addMembershipToGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        return managmentService.createMembershipPlanForGym(membershipPlanRequest);
    }

    public List<MembershipDto> getGymAllMembershipPlans(String gymName) {
        return managmentService.findGymAllMembershipPlans(gymName);
    }

    public Long registerMember(AddMemberRequestDto member) {
        return managmentService.addMemberToMembershipPlan(member);
    }

    public List<MemberDto> getAllMembers() {
        return managmentService.findAllMembers();
    }

    public void cancelMembership(Long memberId){
        managmentService.changeMemberStatusToCancel(memberId);
    }

    public List<RevenueReportDto> getRevenueReport() {
        return managmentService.createRevenueReport();
    }
}
