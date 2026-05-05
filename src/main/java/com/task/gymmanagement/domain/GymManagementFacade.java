package com.task.gymmanagement.domain;

import com.task.gymmanagement.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;
import com.task.gymmanagement.domain.dto.response.RevenueReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymManagementFacade {

    private final GymManagementService managementService;

    public GymDto addGym(AddGymRequestDto gymRequestDto) {
        return managementService.createGym(gymRequestDto);
    }

    public List<GymDto> getAllGyms() {
        return managementService.findAllGyms();
    }

    public MembershipPlanDto addMembershipToGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        return managementService.createMembershipPlanForGym(membershipPlanRequest);
    }

    public List<MembershipPlanDto> getGymAllMembershipPlans(Long gymId) {
        return managementService.findGymAllMembershipPlans(gymId);
    }

    public MemberDto registerMember(AddMemberRequestDto member) {
        return managementService.addMemberToMembershipPlan(member);
    }

    public List<MemberDto> getAllMembers() {
        return managementService.findAllMembers();
    }

    public void cancelMembership(Long memberId){
        managementService.changeMemberStatusToCancel(memberId);
    }

    public List<RevenueReportDto> getRevenueReport() {
        return managementService.createRevenueReport();
    }
}
