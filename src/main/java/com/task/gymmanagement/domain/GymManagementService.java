package com.task.gymmanagement.domain;

import com.task.gymmanagement.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagement.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;
import com.task.gymmanagement.domain.dto.response.RevenueReportDto;
import com.task.gymmanagement.domain.exception.GymAlreadyExistException;
import com.task.gymmanagement.domain.exception.GymNotFoundException;
import com.task.gymmanagement.domain.exception.MemberNotFoundException;
import com.task.gymmanagement.domain.exception.MembershipPlanExceedLimitException;
import com.task.gymmanagement.domain.exception.MembershipPlanNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.task.gymmanagement.domain.DomainMapper.mapDtoToGym;
import static com.task.gymmanagement.domain.DomainMapper.mapDtoToMember;
import static com.task.gymmanagement.domain.DomainMapper.mapToMembershipPlan;

@Service
@RequiredArgsConstructor
@Log4j2
class GymManagementService {
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

        Gym gym = mapDtoToGym(gymRequestDto);

        Gym addedGym = gymRepository.save(gym);
        log.info("Gym with name {} added successfully", gymName);
        return addedGym.getId();
    }

    public List<GymDto> findAllGyms() {
        return gymRepository.findAll().stream()
                .map(DomainMapper::mapGymToDto)
                .toList();
    }

    public Long createMembershipPlanForGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        var gymName = membershipPlanRequest.gymName().trim();
        var gym = gymRepository.findByName(gymName).orElseThrow(() -> new GymNotFoundException(gymName));

        MembershipPlan membershipPlan = mapToMembershipPlan(gym, membershipPlanRequest);

        membershipPlan = membershipPlanRepository.save(membershipPlan);

        log.info("Membership plan {} added successfully with ID: {}", membershipPlan.getName(), membershipPlan.getId());

        return membershipPlan.getId();
    }

    public List<MembershipPlanDto> findGymAllMembershipPlans(String gymName) {
        var gym = gymRepository.findByName(gymName).orElseThrow(() -> new GymNotFoundException(gymName));

        return membershipPlanRepository.findAllByGym(gym).stream()
                .map(DomainMapper::mapMembershipPlanToDto)
                .toList();
    }

    public Long addMemberToMembershipPlan(AddMemberRequestDto dto) {
        var membershipPlanId = dto.membershipId();
        var membershipPlan = membershipPlanRepository.findById(membershipPlanId)
                        .orElseThrow(() -> new MembershipPlanNotFoundException(membershipPlanId));

        var membersCount = memberRepository.countActiveMembersByMembershipPlan(membershipPlan);
        var maxMembers = membershipPlan.getMaxMembers();

        if(membersCount >= maxMembers){
            throw new MembershipPlanExceedLimitException(maxMembers, membershipPlanId);
        }

        var member = mapDtoToMember(dto, membershipPlan);
        member = memberRepository.save(member);

        log.info("Member with ID: {} successfully added to Membership plan with ID: {}",
                member.getId(), membershipPlanId);

        return member.getId();
    }

    public List<MemberDto> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(DomainMapper::mapMemberToDto)
                .toList();
    }

    public void changeMemberStatusToCancel(Long memberId) {
        var member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
        member.cancel();
        memberRepository.save(member);

        log.info("Member with ID: {} successfully cancelled", memberId);
    }

    public List<RevenueReportDto> createRevenueReport() {
        return memberRepository.calculateRevenueReport();
    }
}
