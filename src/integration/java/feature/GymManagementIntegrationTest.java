package feature;

import com.task.gymmanagement.GymManagementApplication;
import com.task.gymmanagement.domain.MembershipType;
import com.task.gymmanagement.domain.dto.response.GymDto;
import com.task.gymmanagement.domain.dto.response.MembershipPlanDto;
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

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GymManagementApplication.class,
        properties = "spring.flyway.clean-disabled=false"
)
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

    @Test
    void should_create_gym_successfully() {
        // given
        var request = getSampleAddGymRequest();

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

    private String getSampleAddGymRequest(){
        return """
                {
                    "name": "PowerGym",
                    "address": "Street 1",
                    "phoneNumber": "+48 123 456 789"
                }
                """;
    }

    @Test
    void should_return_error_when_validation_failed_while_adding_gym(){
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
        var request = getSampleAddGymRequest();

        testClient.post()
                .uri("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        // when & then
        testClient.get()
                .uri("/api/gyms")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<GymDto>>() {})
                .value(gyms -> {
                    assertThat(gyms).hasSize(1);
                    assertThat(gyms.getFirst().name()).isEqualTo("PowerGym");
                });
    }

    @Test
    void should_create_new_membership_plan(){
        // given
        givenGymExists();

        var membershipPlanRequest = getSampleAddMembershipPlanRequest();

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

    private void givenGymExists(){
        var request = getSampleAddGymRequest();

        testClient.post()
                .uri("/api/gyms")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GymDto.class);
    }

    private String getSampleAddMembershipPlanRequest(){
        return """
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
    }

    private MembershipPlanDto getMembershipPlanDto(){
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
    void should_return_error_when_validation_failed_while_adding_membership_plan(){
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
}
