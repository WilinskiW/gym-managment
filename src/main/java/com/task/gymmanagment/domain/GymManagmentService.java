package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
class GymManagmentService {
    private final GymRepository gymRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public Long createGym(AddGymRequestDto gymRequestDto) {
        var gymName = gymRequestDto.name().trim();

        if(gymRepository.existsByName(gymName)){
            log.warn("Gym with name {} already exists", gymName);
            throw new GymAlreadyExistException(gymName);
        }

        if(gymRequestDto.name().isBlank() || gymRequestDto.address().isBlank() || gymRequestDto.phoneNumber().isBlank()){
            throw new IllegalArgumentException("All fields are required");
        }

        Gym gym = mapDtoToGymEntity(gymRequestDto);

        Gym addedGym = gymRepository.save(gym);
        log.info("Gym with name {} added successfully", gymName);
        return addedGym.getId();
    }


    private Gym mapDtoToGymEntity(AddGymRequestDto gymRequestDto) {
        return Gym.builder()
                .name(gymRequestDto.name().trim())
                .address(gymRequestDto.address().trim())
                .phoneNumber(gymRequestDto.phoneNumber().trim())
                .build();
    }

    public List<GymInfoResponseDto> findAllGyms() {
        return gymRepository.findAll().stream()
                .map(GymManagmentService::mapGymToGymInfoDto)
                .toList();
    }

    private static GymInfoResponseDto mapGymToGymInfoDto(Gym gym){
        return GymInfoResponseDto.builder()
                .id(gym.getId())
                .name(gym.getName())
                .address(gym.getAddress())
                .phoneNumber(gym.getPhoneNumber())
                .build();
    }

    public Long createMembershipPlanForGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        var gymName = membershipPlanRequest.gymName().trim();
        var gym = gymRepository.findByName(gymName).orElseThrow(() -> new GymNotFoundException(gymName));

        MembershipPlan membershipPlan = mapDtoToMembershipPlanEntity(gym, membershipPlanRequest);

        membershipPlanRepository.save(membershipPlan);

        log.info("Membership plan {} added successfully", membershipPlan.getName());

        return membershipPlan.getId();
    }

    private static MembershipPlan mapDtoToMembershipPlanEntity(Gym gym, AddMembershipPlanRequestDto request) {
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
}
