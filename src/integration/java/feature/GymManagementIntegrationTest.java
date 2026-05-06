package feature;

import com.task.gymmanagement.GymManagementApplication;
import com.task.gymmanagement.domain.MemberStatus;
import com.task.gymmanagement.domain.MembershipType;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;
import com.task.gymmanagement.domain.dto.response.RevenueReportDto;
import com.task.gymmanagement.infrastructure.error.ErrorResponseDto;
import com.task.gymmanagement.infrastructure.error.ValidationErrorResponseDto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GymManagementApplication.class)
@ActiveProfiles("integration")
@AutoConfigureRestTestClient
public class GymManagementIntegrationTest {
    @Autowired
    private RestTestClient testClient;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    private void givenGymExists() {
        givenGymExists("Power gym", "Street 1");
    }

    private void givenGymExists(String name, String address) {
        String request = """
                {
                    "name": "%s",
                    "address": "%s",
                    "phoneNumber": "+48 111 222 333"
                }
                """.formatted(name, address);

        testClient.post().uri("/api/gyms").contentType(MediaType.APPLICATION_JSON).body(request).exchange();
    }

    private void givenMembershipPlanExists(Long gymId) {
        givenMembershipPlanExists(gymId, "Default Plan", 100, "PLN", 10);
    }

    private void givenMembershipPlanExists(Long gymId, String name, int amount, String currency) {
        givenMembershipPlanExists(gymId, name, amount, currency, 10);
    }

    private void givenMembershipPlanExists(Long gymId, String name, int amount, String currency, int maxMembers) {
        String request = """
                {
                  "name": "%s",
                  "type": "BASIC",
                  "amount": %d,
                  "currency": "%s",
                  "duration": 1,
                  "maxMembers": %d,
                  "gymId": %d
                }
                """.formatted(name, amount, currency, maxMembers, gymId);

        testClient.post().uri("/api/membership-plans").contentType(MediaType.APPLICATION_JSON).body(request).exchange();
    }

    private void givenMemberExists(Long planId) {
        givenMemberExists(planId, "Default Member", "default@test.com");
    }

    private void givenMemberExists(Long planId, String name, String email) {
        String request = """
                {
                    "membershipId": %d,
                    "fullName": "%s",
                    "email": "%s"
                }
                """.formatted(planId, name, email);

        testClient.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON).body(request).exchange();
    }

    @Test
    void should_create_gym_successfully() {
        // given
        var request = """
                {
                    "name": "PowerGym",
                    "address": "Street 1",
                    "phoneNumber": "+48 123 456 789"
                }
                """;

        // when & then
        testClient.post()
                .uri("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GymDto.class)
                .isEqualTo(new GymDto(1L, "PowerGym", "Street 1", "+48 123 456 789"));
    }

    @Test
    void should_return_error_when_validation_failed_while_adding_gym() {
        // given
        var request = """
                {
                    "name": "",
                    "address": "",
                    "phoneNumber": "+48 123 456 78q"
                }
                """;

        // when & then
        testClient.post()
                .uri("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ValidationErrorResponseDto.class)
                .value(body -> assertThat(body.errors())
                        .containsKeys("phoneNumber", "address", "name"));
    }

    @Test
    void should_list_all_gyms() {
        // given
        givenGymExists();

        // when & then
        testClient.get()
                .uri("/api/gyms")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<GymDto>>() {
                })
                .value(gyms -> {
                    assertThat(gyms).hasSize(1);
                    assertThat(gyms.getFirst().name()).isEqualTo("Power gym");
                });
    }

    @Test
    void should_create_new_membership_plan() {
        // given
        givenGymExists();

        var membershipPlanRequest = """
                        {
                          "name": "Basic plan",
                          "type": "BASIC",
                          "amount": 121,
                          "currency": "PLN",
                          "duration": 1,
                          "maxMembers": 1,
                          "gymId": 1
                        }
                """;

        // when & then
        testClient.post()
                .uri("/api/membership-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .body(membershipPlanRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MembershipPlanDto.class)
                .isEqualTo(getMembershipPlanDto());
    }

    private MembershipPlanDto getMembershipPlanDto() {
        return MembershipPlanDto.builder()
                .id(1L)
                .name("Basic plan")
                .type(MembershipType.BASIC)
                .amount(BigDecimal.valueOf(121))
                .currency("PLN")
                .durationMonths(1)
                .maxMembers(1)
                .build();

    }


    @Test
    void should_return_error_when_validation_failed_while_adding_membership_plan() {
        // given
        var request = """
                        {
                          "name": "",
                          "type": "BASIC",
                          "amount": 121,
                          "currency": "PLNa",
                          "duration": -1,
                          "maxMembers": 5,
                          "gymId": 1
                        }
                """;

        // when & then
        testClient.post()
                .uri("/api/membership-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ValidationErrorResponseDto.class)
                .value(body -> assertThat(body.errors())
                        .containsKeys("currency", "name", "duration"));
    }

    @Test
    void should_register_new_member() {
        // given
        givenGymExists();
        givenMembershipPlanExists(1L);
        var request = """
                {
                    "membershipId": 1,
                    "fullName": "Jan Kowalski",
                    "email": "jan.kowalski@example.com"
                }
                """;

        // when & then
        testClient.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MemberDto.class)
                .isEqualTo(new MemberDto(1L, "Jan Kowalski", "Default Plan", MemberStatus.ACTIVE));
    }

    @Test
    void should_cancel_membership() {
        // given
        givenGymExists();
        givenMembershipPlanExists(1L);
        givenMemberExists(1L);

        // when & then
        testClient.patch()
                .uri("/api/members/1/cancel")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void should_generate_revenue_report_for_multiple_gyms_and_plans() {
        // given
        // Gym 1: 2 members in membership plan 100 PLN = 200 PLN
        givenGymExists();
        givenMembershipPlanExists(1L, "Plan 1", 100, "PLN");
        givenMemberExists(1L, "M1", "m1@test.com");
        givenMemberExists(1L, "M2", "m2@test.com");

        // Gym 2: 1 members in membership plan 50 USD = 50 USD
        givenGymExists("Sport gym", "Addr 2");
        givenMembershipPlanExists(2L, "Plan 2", 50, "USD");
        givenMemberExists(2L, "M3", "m3@test.com");

        // when & then
        testClient.get()
                .uri("/api/reports/revenue")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<RevenueReportDto>>() {})
                .value(report -> {
                    assertThat(report).hasSize(2);
                    assertThat(report).extracting(RevenueReportDto::gymName)
                            .containsExactlyInAnyOrder("Power gym", "Sport gym");

                    var gym1Report = report.stream().filter(r -> r.gymName().equals("Power gym")).findFirst().get();
                    assertThat(gym1Report.amount()).isEqualByComparingTo("200");
                    assertThat(gym1Report.currency()).isEqualTo("PLN");

                    var gym2Report = report.stream().filter(r -> r.gymName().equals("Sport gym")).findFirst().get();
                    assertThat(gym2Report.amount()).isEqualByComparingTo("50");
                    assertThat(gym2Report.currency()).isEqualTo("USD");
                });
    }

    @Test
    void should_return_error_when_membership_plan_is_full() {
        // given
        givenGymExists();
        givenMembershipPlanExists(1L, "Full Plan", 100, "PLN", 1);
        givenMemberExists(1L, "First Member", "first@test.com");

        var secondMemberRequest = """
                {
                    "membershipId": 1,
                    "fullName": "Second Member",
                    "email": "second@test.com"
                }
                """;

        // when & then
        testClient.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(secondMemberRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void should_return_error_when_validation_failed_while_adding_member() {
        // given
        var request = """
            {
                "membershipId": 1,
                "fullName": "",
                "email": "not-an-email"
            }
            """;

        // when & then
        testClient.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ValidationErrorResponseDto.class)
                .value(body -> assertThat(body.errors())
                        .containsKeys("fullName", "email"));
    }

    @Test
    void should_return_not_found_when_gym_does_not_exist_while_adding_membership_plan() {
        // given
        var request = """
            {
              "name": "Bad Plan",
              "type": "BASIC",
              "amount": 100,
              "currency": "PLN",
              "duration": 1,
              "maxMembers": 10,
              "gymId": 999
            }
            """;

        // when & then
        testClient.post()
                .uri("/api/membership-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(body ->
                        assertThat(body).isEqualTo(new ErrorResponseDto(404, "Gym with ID: 999 not found")
                        ));
    }

    @Test
    void should_return_not_found_when_membership_plan_does_not_exist_while_adding_member() {
        // given
        var request = """
            {
                "membershipId": 999,
                "fullName": "Jan Kowalski",
                "email": "jan@test.pl"
            }
            """;

        // when & then
        testClient.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(body ->
                        assertThat(body).isEqualTo(new ErrorResponseDto(404, "Membership plan with ID: 999 not found")
                        ));
    }

    @Test
    void should_list_all_membership_plans_for_given_gym() {
        // given
        givenGymExists();
        givenMembershipPlanExists(1L, "Standard", 100, "PLN");
        givenMembershipPlanExists(1L, "Premium", 200, "PLN");

        // when & then
        testClient.get()
                .uri("/api/gyms/1/membership-plans")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MembershipPlanDto>>() {})
                .value(plans -> {
                    assertThat(plans).hasSize(2);
                    assertThat(plans).extracting(MembershipPlanDto::name)
                            .containsExactlyInAnyOrder("Standard", "Premium");
                });
    }

    @Test
    void should_return_not_found_when_requesting_plans_for_non_existent_gym() {
        // when & then
        testClient.get()
                .uri("/api/gyms/999/membership-plans")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(body ->
                        assertThat(body).isEqualTo(new ErrorResponseDto(404, "Gym with ID: 999 not found")
                        ));
    }

    @Test
    void should_list_all_members() {
        // given
        givenGymExists();
        givenMembershipPlanExists(1L);
        givenMemberExists(1L, "Member One", "one@test.pl");
        givenMemberExists(1L, "Member Two", "two@test.pl");

        // when & then
        testClient.get()
                .uri("/api/members")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MemberDto>>() {})
                .value(members -> {
                    assertThat(members).hasSize(2);
                    assertThat(members).extracting(MemberDto::name)
                            .containsExactlyInAnyOrder("Member One", "Member Two");
                });
    }
}