package com.task.gymmanagement.domain;

import com.task.gymmanagement.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;

class DomainMapper {
    static Gym mapDtoToGym(AddGymRequestDto gymRequestDto) {
        return Gym.builder()
                .name(gymRequestDto.name().trim())
                .address(gymRequestDto.address().trim())
                .phoneNumber(gymRequestDto.phoneNumber().trim())
                .build();
    }

    static GymDto mapGymToDto(Gym gym) {
        return GymDto.builder()
                .id(gym.getId())
                .name(gym.getName())
                .address(gym.getAddress())
                .phoneNumber(gym.getPhoneNumber())
                .build();
    }

    static MembershipPlan mapToMembershipPlan(Gym gym, AddMembershipPlanRequestDto request) {
        return MembershipPlan.builder()
                .name(request.name().trim())
                .gym(gym)
                .type(request.type())
                .amount(request.amount())
                .currency(request.currency().trim())
                .durationMonths(request.duration())
                .maxMembers(request.maxMembers())
                .build();
    }

    static MembershipPlanDto mapMembershipPlanToDto(MembershipPlan membershipPlan) {
        return MembershipPlanDto.builder()
                .id(membershipPlan.getId())
                .name(membershipPlan.getName())
                .type(membershipPlan.getType())
                .amount(membershipPlan.getAmount())
                .currency(membershipPlan.getCurrency())
                .durationMonths(membershipPlan.getDurationMonths())
                .maxMembers(membershipPlan.getMaxMembers())
                .build();
    }

    static Member mapDtoToMember(AddMemberRequestDto dto, MembershipPlan membershipPlan){
        return Member.builder()
                .membershipPlan(membershipPlan)
                .fullName(dto.fullName().trim())
                .email(dto.email().trim())
                .build();
    }

    static MemberDto mapMemberToDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getFullName())
                .membershipPlan(member.getMembershipPlan().getName())
                .status(member.getStatus())
                .build();
    }
}
