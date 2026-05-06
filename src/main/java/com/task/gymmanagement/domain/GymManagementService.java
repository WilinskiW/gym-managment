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
import com.task.gymmanagement.domain.exception.MemberAlreadyExistsInGymException;
import com.task.gymmanagement.domain.exception.MemberNotFoundException;
import com.task.gymmanagement.domain.exception.MembershipPlanAlreadyCancelledException;
import com.task.gymmanagement.domain.exception.MembershipPlanExceedLimitException;
import com.task.gymmanagement.domain.exception.MembershipPlanNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.task.gymmanagement.domain.DomainMapper.mapDtoToGym;
import static com.task.gymmanagement.domain.DomainMapper.mapDtoToMember;
import static com.task.gymmanagement.domain.DomainMapper.mapGymToDto;
import static com.task.gymmanagement.domain.DomainMapper.mapMemberToDto;
import static com.task.gymmanagement.domain.DomainMapper.mapMembershipPlanToDto;
import static com.task.gymmanagement.domain.DomainMapper.mapToMembershipPlan;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
class GymManagementService {
    private final GymRepository gymRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberRepository memberRepository;

    public GymDto createGym(AddGymRequestDto gymRequestDto) {
        var gymName = gymRequestDto.name().trim();

        if (gymRepository.existsByName(gymName)) {
            log.warn("Gym with name {} already exists", gymName);
            throw new GymAlreadyExistException(gymName);
        }

        var gym = mapDtoToGym(gymRequestDto);

        gymRepository.save(gym);
        log.info("Gym with name {} added successfully", gymName);

        return mapGymToDto(gym);
    }

    @Transactional(readOnly = true)
    public List<GymDto> findAllGyms() {
        return gymRepository.findAll().stream()
                .map(DomainMapper::mapGymToDto)
                .toList();
    }

    public MembershipPlanDto createMembershipPlanForGym(AddMembershipPlanRequestDto membershipPlanRequest) {
        var gymId = membershipPlanRequest.gymId();
        var gym = gymRepository.findById(gymId).orElseThrow(() -> new GymNotFoundException(gymId));

        MembershipPlan membershipPlan = mapToMembershipPlan(gym, membershipPlanRequest);

        membershipPlanRepository.save(membershipPlan);

        log.info("Membership plan {} added successfully with ID: {}", membershipPlan.getName(), membershipPlan.getId());

        return mapMembershipPlanToDto(membershipPlan);
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanDto> findGymAllMembershipPlans(Long id) {
        var gym = gymRepository.findById(id).orElseThrow(() -> new GymNotFoundException(id));

        return membershipPlanRepository.findAllByGym(gym).stream()
                .map(DomainMapper::mapMembershipPlanToDto)
                .toList();
    }

    public MemberDto addMemberToMembershipPlan(AddMemberRequestDto dto) {
        var membershipPlanId = dto.membershipId();
        var plan = getMembershipPlanOrThrow(membershipPlanId);


        if (isEmailActiveInThisGym(dto, plan)) {
            throw new MemberAlreadyExistsInGymException(dto.email(), plan.getGym().getName(), plan.getGym().getId());
        }

        validateMembershipPlanCapacity(plan, membershipPlanId);

        var member = memberRepository.save(mapDtoToMember(dto, plan));

        log.info("Member with ID: {} successfully added to Membership plan with ID: {}",
                member.getId(), dto.membershipId());

        return mapMemberToDto(member);
    }

    private boolean isEmailActiveInThisGym(AddMemberRequestDto dto, MembershipPlan plan) {
        return memberRepository.findAllByEmail(dto.email()).stream()
                .anyMatch(m ->
                        m.getMembershipPlan().getGym().getId().equals(plan.getGym().getId())
                                && m.getStatus() == MemberStatus.ACTIVE
                );
    }

    private MembershipPlan getMembershipPlanOrThrow(Long membershipPlanId) {
        return membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new MembershipPlanNotFoundException(membershipPlanId));
    }

    private void validateMembershipPlanCapacity(MembershipPlan membershipPlan, Long membershipPlanId) {
        var membersCount = memberRepository.countActiveMembersByMembershipPlan(membershipPlan);
        var maxMembers = membershipPlan.getMaxMembers();

        if (membersCount >= maxMembers) {
            throw new MembershipPlanExceedLimitException(maxMembers, membershipPlanId);
        }
    }

    @Transactional(readOnly = true)
    public List<MemberDto> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(DomainMapper::mapMemberToDto)
                .toList();
    }

    public void changeMemberStatusToCancel(Long memberId) {
        var member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.isMemberAlreadyCancelled()) {
            throw new MembershipPlanAlreadyCancelledException(memberId);
        }

        member.cancel();
        memberRepository.save(member);

        log.info("Member with ID: {} successfully cancelled", memberId);
    }

    public List<RevenueReportDto> createRevenueReport() {
        return memberRepository.calculateRevenueReport();
    }
}
