package com.task.gymmanagment.domain;

import com.task.gymmanagment.domain.dto.request.AddGymRequestDto;
import com.task.gymmanagment.domain.dto.request.AddMembershipPlanRequestDto;
import com.task.gymmanagment.domain.dto.response.GymInfoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GymManagmentFacadeTest {
    private GymManagmentFacade gymManagmentFacade = createFacade();

    @BeforeEach
    void setUp() {
        gymManagmentFacade = createFacade();
    }

    private static GymManagmentFacade createFacade() {
        return new GymManagmentFacade(new GymManagmentService
                (
                        new SimpleInMemoryGymRepository(),
                        new SimpleInMemoryMembershipPlanRepository()
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
        Long addedId = gymManagmentFacade.addGym(request);

        // then
        assertThat(addedId).isEqualTo(1);
    }

    @Test
    void should_throw_exception_if_gym_already_exists() {
        // given
        var firstRequest = createAddGymRequest("Test gym");
        var secondRequest = createAddGymRequest("  Test gym  ");

        // when
        gymManagmentFacade.addGym(firstRequest);

        // then
        assertThatThrownBy(() -> gymManagmentFacade.addGym(secondRequest))
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
        assertThatThrownBy(() -> gymManagmentFacade.addGym(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("All fields are required");
    }

    @Test
    void should_list_all_gyms() {
        // given
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 1"));
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 2"));
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 3"));

        // when
        List<GymInfoResponseDto> gyms = gymManagmentFacade.getAllGyms();

        // then
        assertThat(gyms).hasSize(3);
        assertThat(gyms.getFirst().name()).isEqualTo("Test gym 1");
        assertThat(gyms.get(1).name()).isEqualTo("Test gym 2");
        assertThat(gyms.getLast().name()).isEqualTo("Test gym 3");
    }

    @Test
    void should_add_new_membership_to_existing_gym() {
        // given
        gymManagmentFacade.addGym(createAddGymRequest("Test gym"));

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
        Long addedMembershipId = gymManagmentFacade.addMembershipToGym(membershipPlanRequest);

        // then
        assertThat(addedMembershipId).isEqualTo(1);
    }

    @Test
    void should_throw_exception_when_membership_plan_want_to_be_added_while_gym_doesnt_exist(){
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
        assertThatThrownBy(() -> gymManagmentFacade.addMembershipToGym(membershipPlanRequest))
                .isInstanceOf(GymNotFoundException.class)
                .hasMessage("Gym with name Test gym not found");
    }

    @Test
    void should_list_all_membership_plans_for_existing_gym(){
        // given
        gymManagmentFacade.addGym(createAddGymRequest("Test gym"));

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


        gymManagmentFacade.addMembershipToGym(membershipPlan1);
        gymManagmentFacade.addMembershipToGym(membershipPlan2);

        // when
        var membershipPlans = gymManagmentFacade.getGymAllMembershipPlans("Test gym");

        // then
        assertThat(membershipPlans).hasSize(2);
        assertThat(membershipPlans.getFirst().currency()).isEqualTo("USD");
        assertThat(membershipPlans.getLast().currency()).isEqualTo("PLN");
    }

    @Test
    void should_throw_exception_when_gym_doesnt_exist_while_trying_to_list_membership_plans(){
        assertThatThrownBy(() ->  gymManagmentFacade.getGymAllMembershipPlans("Test gym"))
                .isInstanceOf(GymNotFoundException.class)
                .hasMessage("Gym with name Test gym not found");
    }
}
