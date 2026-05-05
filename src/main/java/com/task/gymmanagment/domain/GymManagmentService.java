package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import com.task.gymmanagment.domain.dto.response.MembershipPlanInfoResponseDto;
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
    private final MemberRepository memberRepository;

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

        membershipPlan = membershipPlanRepository.save(membershipPlan);

        log.info("Membership plan {} added successfully with ID: {}", membershipPlan.getName(), membershipPlan.getId());

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

    public List<MembershipPlanInfoResponseDto> findGymAllMembershipPlans(String gymName) {
        var gym = gymRepository.findByName(gymName).orElseThrow(() -> new GymNotFoundException(gymName));

        return membershipPlanRepository.findAllByGym(gym).stream()
                .map(GymManagmentService::mapMembershipPlanToDto)
                .toList();
    }

    private static MembershipPlanInfoResponseDto mapMembershipPlanToDto(MembershipPlan membershipPlan) {
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

    public Long addMemberToMembershipPlan(AddMemberRequestDto dto) {
        var membershipPlanId = dto.membershipId();
        var membershipPlan = membershipPlanRepository.findById(membershipPlanId)
                        .orElseThrow(() -> new MembershipPlanNotFoundException(membershipPlanId));

        var membersCount = memberRepository.countMembersByMembershipPlan(membershipPlan);
        var maxMembers = membershipPlan.getMaxMembers();

        if(membersCount >= maxMembers){
            throw new MembershipPlanExceedLimitException(maxMembers, membershipPlanId);
        }

        var member = mapDtoToMemberEntity(dto, membershipPlan);
        member = memberRepository.save(member);

        log.info("Member with ID: {} successfully added to Membership plan with ID: {}",
                member.getId(), membershipPlanId);

        return member.getId();
    }

    private Member mapDtoToMemberEntity(AddMemberRequestDto dto, MembershipPlan membershipPlan){
        return Member.builder()
                .membershipPlan(membershipPlan)
                .fullName(dto.fullName())
                .email(dto.email())
                .build();

    }
}
