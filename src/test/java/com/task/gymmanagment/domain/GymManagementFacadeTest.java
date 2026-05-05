package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMemberRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymDto;
import com.task.gymmanagment.domain.dto.response.MemberDto;
import com.task.gymmanagment.domain.exception.GymAlreadyExistException;
import com.task.gymmanagment.domain.exception.GymNotFoundException;
import com.task.gymmanagment.domain.exception.MemberNotFoundException;
import com.task.gymmanagment.domain.exception.MembershipPlanExceedLimitException;
import com.task.gymmanagment.domain.exception.MembershipPlanNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GymManagementFacadeTest {
    private GymManagementFacade facade = createFacade();

    @BeforeEach
    void setUp() {
        facade = createFacade();
    }

    private static GymManagementFacade createFacade() {
        return new GymManagementFacade(new GymManagementService
                (
                        new SimpleInMemoryGymRepository(),
                        new SimpleInMemoryMembershipPlanRepository(),
                        new SimpleInMemoryMemberRepository()
                ));
    }

    @Test
    void should_add_gym_if_doesnt_exist() {
        // given
        var request = AddGymRequestDto.builder()
                .name("Test gym")
                .address("Test addres")
                .phoneNumber("123456789")
                .build();

        // when
        Long addedId = facade.addGym(request);

        // then
        assertThat(addedId).isEqualTo(1);
    }

    @Test
    void should_throw_exception_if_gym_already_exists() {
        // given
        var firstRequest = createAddGymRequest("Test gym");
        var secondRequest = createAddGymRequest("  Test gym  ");

        // when
        facade.addGym(firstRequest);

        // then
        assertThatThrownBy(() -> facade.addGym(secondRequest))
                .isInstanceOf(GymAlreadyExistException.class)
                .hasMessage("Gym with name Test gym already exists");
    }

    private AddGymRequestDto createAddGymRequest(String name) {
        return AddGymRequestDto.builder()
                .name(name)
                .address("Test address")
                .phoneNumber("123456789")
                .build();
    }

    @Test
    void should_throw_exception_when_address_is_blank() {
        // given
        var request = AddGymRequestDto.builder()
                .name("Test gym")
                .address("   ")
                .phoneNumber("123456789")
                .build();

        // when & then
        assertThatThrownBy(() -> facade.addGym(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("All fields are required");
    }

    @Test
    void should_list_all_gyms() {
        // given
        facade.addGym(createAddGymRequest("Test gym 1"));
        facade.addGym(createAddGymRequest("Test gym 2"));
        facade.addGym(createAddGymRequest("Test gym 3"));

        // when
        List<GymDto> gyms = facade.getAllGyms();

        // then
        assertThat(gyms).hasSize(3);
        assertThat(gyms.getFirst().name()).isEqualTo("Test gym 1");
        assertThat(gyms.get(1).name()).isEqualTo("Test gym 2");
        assertThat(gyms.getLast().name()).isEqualTo("Test gym 3");
    }

    @Test
    void should_add_new_membership_to_existing_gym() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlanRequest = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(1)
                .gymName("Test gym")
                .build();

        // when
        Long addedMembershipId = facade.addMembershipToGym(membershipPlanRequest);

        // then
        assertThat(addedMembershipId).isEqualTo(1);
    }

    @Test
    void should_throw_exception_when_membership_plan_want_to_be_added_while_gym_doesnt_exist() {
        // given
        var membershipPlanRequest = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(1)
                .gymName("Test gym")
                .build();

        // when & then
        assertThatThrownBy(() -> facade.addMembershipToGym(membershipPlanRequest))
                .isInstanceOf(GymNotFoundException.class)
                .hasMessage("Gym with name Test gym not found");
    }

    @Test
    void should_list_all_membership_plans_for_existing_gym() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan1 = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(1)
                .gymName("Test gym")
                .build();

        var membershipPlan2 = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(500))
                .currency("PLN")
                .duration(1)
                .maxMembers(1)
                .gymName("Test gym")
                .build();


        facade.addMembershipToGym(membershipPlan1);
        facade.addMembershipToGym(membershipPlan2);

        // when
        var membershipPlans = facade.getGymAllMembershipPlans("Test gym");

        // then
        assertThat(membershipPlans).hasSize(2);
        assertThat(membershipPlans.getFirst().currency()).isEqualTo("USD");
        assertThat(membershipPlans.getLast().currency()).isEqualTo("PLN");
    }

    @Test
    void should_throw_exception_when_gym_doesnt_exist_while_trying_to_list_membership_plans() {
        assertThatThrownBy(() -> facade.getGymAllMembershipPlans("Test gym"))
                .isInstanceOf(GymNotFoundException.class)
                .hasMessage("Gym with name Test gym not found");
    }

    @Test
    void should_add_new_members_to_existing_membership_plan_when_members_amount_is_in_bound() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(2)
                .gymName("Test gym")
                .build();

        facade.addMembershipToGym(membershipPlan);

        var member1 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        var member2 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Bill Nowak")
                .email("test@example.com")
                .build();

        // when
        Long member1Id = facade.registerMember(member1);
        Long member2Id = facade.registerMember(member2);

        // then
        assertThat(member1Id).isEqualTo(1);
        assertThat(member2Id).isEqualTo(2);
    }

    @Test
    void should_throw_exception_when_new_member_is_out_of_bound() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(1)
                .gymName("Test gym")
                .build();

        facade.addMembershipToGym(membershipPlan);

        var member1 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        var member2 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Bill Nowak")
                .email("test@example.com")
                .build();

        facade.registerMember(member1);

        // when & then
        assertThatThrownBy(() -> facade.registerMember(member2))
                .isInstanceOf(MembershipPlanExceedLimitException.class)
                .hasMessage("Exceeded maximum members: 1 for a given Membership plan with ID: 1");
    }

    @Test
    void should_throw_exception_membership_plan_not_found_while_adding_new_member() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var member = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        // when & then
        assertThatThrownBy(() -> facade.registerMember(member))
                .isInstanceOf(MembershipPlanNotFoundException.class)
                .hasMessage("Membership plan with ID: 1 not found");
    }

    @Test
    void should_list_all_members() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(2)
                .gymName("Test gym")
                .build();

        facade.addMembershipToGym(membershipPlan);

        var member1 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        var member2 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Bill Nowak")
                .email("test@example.com")
                .build();

        facade.registerMember(member1);
        facade.registerMember(member2);

        // when
        List<MemberDto> dtos = facade.getAllMembers();

        // then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.getFirst())
                .matches(dto -> dto.name().equals("Jan Kowalski"))
                .matches(dto -> dto.membershipPlan().equals("Test plan"))
                .matches(dto -> dto.status().equals(MemberStatus.ACTIVE));

        assertThat(dtos.getLast())
                .matches(dto -> dto.name().equals("Bill Nowak"))
                .matches(dto -> dto.membershipPlan().equals("Test plan"))
                .matches(dto -> dto.status().equals(MemberStatus.ACTIVE));

    }

    @Test
    void should_return_empty_list_when_there_are_no_members() {
        // when
        List<MemberDto> dtos = facade.getAllMembers();

        // then
        assertThat(dtos).isEmpty();
    }

    @Test
    void should_canceled_membership_plan_for_specify_member() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(2)
                .gymName("Test gym")
                .build();

        facade.addMembershipToGym(membershipPlan);

        var member = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        facade.registerMember(member);

        // when
        facade.cancelMembership(1L);

        // then
        List<MemberDto> dtos = facade.getAllMembers();
        assertThat(dtos.getFirst().status()).isEqualTo(MemberStatus.CANCELLED);
    }

    @Test
    void should_throw_exception_when_trying_to_cancel_non_existing_member() {
        // when & then
        assertThatThrownBy(() -> facade.cancelMembership(1L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("Member with ID: 1 not found");
    }

    @Test
    void should_return_correct_revenue() {
        // given
        facade.addGym(createAddGymRequest("Test gym"));

        var membershipPlan1 = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .duration(1)
                .maxMembers(2)
                .gymName("Test gym")
                .build();

        var membershipPlan2 = AddMembershipPlanRequestDto.builder()
                .name("Test plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(120))
                .currency("GBP")
                .duration(1)
                .maxMembers(2)
                .gymName("Test gym")
                .build();

        facade.addMembershipToGym(membershipPlan1);
        facade.addMembershipToGym(membershipPlan2);

        var member1 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        var member2 = AddMemberRequestDto.builder()
                .membershipId(1L)
                .fullName("Jan Kowalski")
                .email("test@gmail.com")
                .build();

        var member3 = AddMemberRequestDto.builder()
                .membershipId(2L)
                .fullName("Bill Clinton")
                .email("test@example.com")
                .build();

        facade.registerMember(member1);
        facade.registerMember(member2);
        facade.registerMember(member3);

        // when
        var revenueReport = facade.getRevenueReport();

        // then
        assertThat(revenueReport).hasSize(2);

        assertThat(revenueReport.getFirst())
                .matches(r -> r.gymName().equals("Test gym"))
                .matches(r -> r.amount().equals(BigDecimal.valueOf(120)))
                .matches(r -> r.currency().equals("GBP"));

        assertThat(revenueReport.getLast())
                .matches(r -> r.gymName().equals("Test gym"))
                .matches(r -> r.amount().equals(BigDecimal.valueOf(200)))
                .matches(r -> r.currency().equals("USD"));
    }
}
