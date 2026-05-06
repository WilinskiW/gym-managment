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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

public class GymManagementFacadeTest {
    private static final Long FIRST_GYM_ID = 1L;
    private static final Long FIRST_MEMBERSHIP_PLAN_ID = 1L;
    private static final Long FIRST_MEMBER_ID = 1L;

    private static final String DEFAULT_GYM_NAME = "Test gym";
    private static final String DEFAULT_GYM_ADDRESS = "Test address";
    private static final String DEFAULT_PHONE_NUMBER = "123456789";

    private static final String DEFAULT_PLAN_NAME = "Test plan";
    private static final MembershipType DEFAULT_MEMBERSHIP_TYPE = MembershipType.BASIC;
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(100);
    private static final String DEFAULT_CURRENCY = "USD";
    private static final int DEFAULT_DURATION = 1;
    private static final int DEFAULT_MAX_MEMBERS = 2;

    private static final String DEFAULT_MEMBER_NAME = "Jan Kowalski";
    private static final String DEFAULT_MEMBER_EMAIL = "test@gmail.com";

    private GymManagementFacade facade = createFacade();

    @BeforeEach
    void setUp() {
        facade = createFacade();
    }

    private static GymManagementFacade createFacade() {
        return new GymManagementFacade(new GymManagementService(
                new SimpleInMemoryGymRepository(),
                new SimpleInMemoryMembershipPlanRepository(),
                new SimpleInMemoryMemberRepository()
        ));
    }

    private void givenGymExists() {
        facade.addGym(gymRequest(DEFAULT_GYM_NAME));
    }

    private void givenGymExists(String name) {
        facade.addGym(gymRequest(name));
    }

    private void givenMembershipPlanExists() {
        givenGymExists();
        facade.addMembershipToGym(membershipPlanRequest(FIRST_GYM_ID));
    }

    private void givenMembershipPlanExists(int maxMembers) {
        givenGymExists();
        facade.addMembershipToGym(membershipPlanRequest(FIRST_GYM_ID, maxMembers));
    }

    private void givenMemberExists() {
        givenMembershipPlanExists();
        facade.registerMember(memberRequest(FIRST_MEMBERSHIP_PLAN_ID));
    }

    private AddGymRequestDto gymRequest(String name) {
        return AddGymRequestDto.builder()
                .name(name)
                .address(DEFAULT_GYM_ADDRESS)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .build();
    }

    private AddMembershipPlanRequestDto membershipPlanRequest(Long gymId) {
        return membershipPlanRequest(gymId, DEFAULT_MAX_MEMBERS);
    }

    private AddMembershipPlanRequestDto membershipPlanRequest(Long gymId, int maxMembers) {
        return AddMembershipPlanRequestDto.builder()
                .name(DEFAULT_PLAN_NAME)
                .type(DEFAULT_MEMBERSHIP_TYPE)
                .amount(DEFAULT_AMOUNT)
                .currency(DEFAULT_CURRENCY)
                .duration(DEFAULT_DURATION)
                .maxMembers(maxMembers)
                .gymId(gymId)
                .build();
    }

    private AddMembershipPlanRequestDto membershipPlanRequest(Long gymId, BigDecimal amount, String currency) {
        return AddMembershipPlanRequestDto.builder()
                .name(DEFAULT_PLAN_NAME)
                .type(DEFAULT_MEMBERSHIP_TYPE)
                .amount(amount)
                .currency(currency)
                .duration(DEFAULT_DURATION)
                .maxMembers(DEFAULT_MAX_MEMBERS)
                .gymId(gymId)
                .build();
    }

    private AddMemberRequestDto memberRequest(Long membershipPlanId) {
        return memberRequest(membershipPlanId, DEFAULT_MEMBER_NAME, DEFAULT_MEMBER_EMAIL);
    }

    private AddMemberRequestDto memberRequest(Long membershipPlanId, String fullName, String email) {
        return AddMemberRequestDto.builder()
                .membershipId(membershipPlanId)
                .fullName(fullName)
                .email(email)
                .build();
    }

    @Nested
    class GymTests {
        @Test
        @DisplayName("Should add gym if doesn't exists")
        void should_add_gym_if_doesnt_exist() {
            // given
            var request = gymRequest(DEFAULT_GYM_NAME);

            // when
            var dto = facade.addGym(request);

            // then
            assertThat(dto.id()).isEqualTo(FIRST_GYM_ID);
        }

        @Test
        @DisplayName("Should throw exception if gym already exists")
        void should_throw_exception_if_gym_already_exists() {
            // given
            var firstRequest = gymRequest(DEFAULT_GYM_NAME);
            var secondRequest = gymRequest(DEFAULT_GYM_NAME);

            // when
            facade.addGym(firstRequest);

            // then
            assertThatThrownBy(() -> facade.addGym(secondRequest))
                    .isInstanceOf(GymAlreadyExistException.class)
                    .hasMessage("Gym with name Test gym already exists");
        }

        @Test
        @DisplayName("Should list all existing gyms")
        void should_list_all_gyms() {
            // given
            givenGymExists();
            givenGymExists("Test gym 2");
            givenGymExists("Test gym 3");

            // when
            List<GymDto> gyms = facade.getAllGyms();

            // then
            assertThat(gyms).hasSize(3);
            assertThat(gyms).
                    extracting(GymDto::name)
                    .containsExactlyInAnyOrder("Test gym", "Test gym 2", "Test gym 3");
        }
    }

    @Nested
    class MembershipPlanTests {
        @Test
        @DisplayName("Should add new membership plan to existing gym")
        void should_add_new_membership_to_existing_gym() {
            // given
            givenGymExists();

            // when
            var dto = facade.addMembershipToGym(membershipPlanRequest(FIRST_GYM_ID));

            // then
            assertThat(dto.id()).isEqualTo(FIRST_MEMBERSHIP_PLAN_ID);
        }

        @Test
        @DisplayName("Should throw exception when trying to add membership plan to non-existing gym")
        void should_throw_exception_when_membership_plan_want_to_be_added_while_gym_doesnt_exist() {
            // given
            var membershipPlanRequest = membershipPlanRequest(FIRST_GYM_ID);

            // when & then
            assertThatThrownBy(() -> facade.addMembershipToGym(membershipPlanRequest))
                    .isInstanceOf(GymNotFoundException.class)
                    .hasMessage("Gym with ID: 1 not found");
        }

        @Test
        @DisplayName("Should list all membership plans for existing gym")
        void should_list_all_membership_plans_for_existing_gym() {
            // given
            givenMembershipPlanExists();

            facade.addMembershipToGym(membershipPlanRequest(
                    FIRST_GYM_ID,
                    BigDecimal.valueOf(500),
                    "PLN"
            ));

            // when
            var membershipPlans = facade.getGymAllMembershipPlans(FIRST_GYM_ID);

            // then
            assertThat(membershipPlans)
                    .hasSize(2)
                    .extracting(MembershipPlanDto::currency)
                    .containsExactlyInAnyOrder("USD", "PLN");
        }

        @Test
        @DisplayName("Should throw exception when trying to list membership plans for non-existing gym")
        void should_throw_exception_when_gym_doesnt_exist_while_trying_to_list_membership_plans() {
            // when & then
            assertThatThrownBy(() -> facade.getGymAllMembershipPlans(FIRST_GYM_ID))
                    .isInstanceOf(GymNotFoundException.class)
                    .hasMessage("Gym with ID: 1 not found");
        }
    }

    @Nested
    class MemberTests {
        @Test
        @DisplayName("Should add new member to existing membership plan when limit is not exceeded")
        void should_add_new_members_to_existing_membership_plan_when_members_amount_is_in_bound() {
            // given
            givenMembershipPlanExists();

            var firstMember = memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Jan Kowalski",
                    "test@gmail.com"
            );

            var secondMember = memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Bill Nowak",
                    "test@example.com"
            );

            // when
            var firstMemberDto = facade.registerMember(firstMember);
            var secondMemberDto = facade.registerMember(secondMember);

            // then
            assertThat(firstMemberDto.id()).isEqualTo(FIRST_MEMBER_ID);
            assertThat(secondMemberDto.id()).isEqualTo(2L);
        }

        @Test
        @DisplayName("Should throw exception when adding new member to existing membership plan when limit is exceeded")
        void should_throw_exception_when_new_member_is_out_of_bound() {
            // given
            givenMembershipPlanExists(1);

            facade.registerMember(memberRequest(FIRST_MEMBERSHIP_PLAN_ID));

            var secondMember = memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Bill Nowak",
                    "test@example.com"
            );

            // when & then
            assertThatThrownBy(() -> facade.registerMember(secondMember))
                    .isInstanceOf(MembershipPlanExceedLimitException.class)
                    .hasMessage("Exceeded maximum members: 1 for a given Membership plan with ID: 1");
        }

        @Test
        @DisplayName("Should throw exception when adding new member to non-existing membership plan")
        void should_throw_exception_membership_plan_not_found_while_adding_new_member() {
            // given
            givenGymExists();

            var member = memberRequest(FIRST_MEMBERSHIP_PLAN_ID);

            // when & then
            assertThatThrownBy(() -> facade.registerMember(member))
                    .isInstanceOf(MembershipPlanNotFoundException.class)
                    .hasMessage("Membership plan with ID: 1 not found");
        }

        @Test
        @DisplayName("Should throw exception when adding member with existing email in gym")
        void should_throw_exception_when_adding_member_with_existing_email_in_gym() {
            // given
            givenMembershipPlanExists();

            var member = memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Jan Kowalski",
                    "test@gmail.com"
            );

            facade.registerMember(member);
            // when & then
            assertThatThrownBy(() -> facade.registerMember(member))
                    .isInstanceOf(MemberAlreadyExistsInGymException.class)
                    .hasMessage("Member with email and active plan: test@gmail.com already exists in gym: Test gym (ID: 1)");
        }

        @Test
        @DisplayName("Should list all members for existing membership plan")
        void should_list_all_members() {
            // given
            givenMembershipPlanExists();

            facade.registerMember(memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Jan Kowalski",
                    "test@gmail.com"
            ));

            facade.registerMember(memberRequest(
                    FIRST_MEMBERSHIP_PLAN_ID,
                    "Bill Nowak",
                    "test@example.com"
            ));

            // when
            List<MemberDto> members = facade.getAllMembers();

            // then
            assertThat(members)
                    .hasSize(2)
                    .extracting(MemberDto::name, MemberDto::membershipPlan, MemberDto::status)
                    .containsExactlyInAnyOrder(
                            tuple("Jan Kowalski", DEFAULT_PLAN_NAME, MemberStatus.ACTIVE),
                            tuple("Bill Nowak", DEFAULT_PLAN_NAME, MemberStatus.ACTIVE)
                    );
        }

        @Test
        @DisplayName("Should return empty list when there are no members")
        void should_return_empty_list_when_there_are_no_members() {
            // when
            List<MemberDto> members = facade.getAllMembers();

            // then
            assertThat(members).isEmpty();
        }

        @Test
        @DisplayName("Should cancel membership for specify member")
        void should_cancel_membership_for_specify_member() {
            // given
            givenMemberExists();

            // when
            facade.cancelMembership(FIRST_MEMBER_ID);

            // then
            assertThat(facade.getAllMembers())
                    .singleElement()
                    .extracting(MemberDto::status)
                    .isEqualTo(MemberStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should throw exception when trying to cancel member who was already cancelled")
        void should_throw_exception_when_trying_to_cancel_member_who_was_already_cancelled(){
            // given
            givenMemberExists();
            facade.cancelMembership(FIRST_MEMBER_ID);

            // when & then
            assertThatThrownBy(() -> facade.cancelMembership(FIRST_MEMBER_ID))
                    .isInstanceOf(MembershipPlanAlreadyCancelledException.class)
                    .hasMessage("Member with ID: 1 is already cancelled");
        }

        @Test
        @DisplayName("Should throw exception when trying to cancel non-existing member")
        void should_throw_exception_when_trying_to_cancel_non_existing_member() {
            // when & then
            assertThatThrownBy(() -> facade.cancelMembership(FIRST_MEMBER_ID))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage("Member with ID: 1 not found");
        }
    }

    @Nested
    class RevenueReportTests {
        @Test
        @DisplayName("Should return correct revenue")
        void should_return_correct_revenue() {
            // given
            givenGymExists();

            facade.addMembershipToGym(membershipPlanRequest(
                    FIRST_GYM_ID,
                    BigDecimal.valueOf(100),
                    "USD"
            ));

            facade.addMembershipToGym(membershipPlanRequest(
                    FIRST_GYM_ID,
                    BigDecimal.valueOf(120),
                    "GBP"
            ));

            facade.registerMember(memberRequest(
                    1L,
                    "Jan Kowalski",
                    "test@gmail.com"
            ));

            facade.registerMember(memberRequest(
                    1L,
                    "Anna Nowak",
                    "anna.nowak@example.com"
            ));

            facade.registerMember(memberRequest(
                    2L,
                    "Bill Clinton",
                    "test@example.com"
            ));

            // when
            var revenueReport = facade.getRevenueReport();

            // then
            assertThat(revenueReport)
                    .hasSize(2)
                    .extracting(
                            RevenueReportDto::gymName,
                            RevenueReportDto::amount,
                            RevenueReportDto::currency
                    )
                    .containsExactlyInAnyOrder(
                            tuple(DEFAULT_GYM_NAME, BigDecimal.valueOf(200), "USD"),
                            tuple(DEFAULT_GYM_NAME, BigDecimal.valueOf(120), "GBP")
                    );
        }
    }
}
