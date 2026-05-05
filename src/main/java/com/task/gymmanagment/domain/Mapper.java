package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import com.task.gymmanagment.domain.dto.response.MembershipPlanInfoResponseDto;

class Mapper {
    static Gym mapDtoToGymEntity(AddGymRequestDto gymRequestDto) {
        return Gym.builder()
                .name(gymRequestDto.name().trim())
                .address(gymRequestDto.address().trim())
                .phoneNumber(gymRequestDto.phoneNumber().trim())
                .build();
    }

    static GymInfoResponseDto mapGymToGymInfoDto(Gym gym) {
        return GymInfoResponseDto.builder()
                .id(gym.getId())
                .name(gym.getName())
                .address(gym.getAddress())
                .phoneNumber(gym.getPhoneNumber())
                .build();
    }

    static MembershipPlan mapDtoToMembershipPlanEntity(Gym gym, AddMembershipPlanRequestDto request) {
        return MembershipPlan.builder()
                .name(request.name())
                .gym(gym)
                .type(request.type())
                .amount(request.amount())
                .currency(request.currency())
                .durationMonths(request.duration())
                .maxMembers(request.maxMembers())
                .build();
    }

    static Member mapDtoToMemberEntity(AddMemberRequestDto dto, MembershipPlan membershipPlan){
        return Member.builder()
                .membershipPlan(membershipPlan)
                .fullName(dto.fullName())
                .email(dto.email())
                .build();

    }

    static MembershipPlanInfoResponseDto mapMembershipPlanToDto(MembershipPlan membershipPlan) {
        return MembershipPlanInfoResponseDto.builder()
                .id(membershipPlan.getId())
                .name(membershipPlan.getName())
                .type(membershipPlan.getType())
                .amount(membershipPlan.getAmount())
                .currency(membershipPlan.getCurrency())
                .durationMonths(membershipPlan.getDurationMonths())
                .maxMembers(membershipPlan.getMaxMembers())
                .build();
    }
}
