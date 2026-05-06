package feature;

import com.task.gymmanagement.domain.MemberStatus;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.infrastructure.error.ValidationErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberControllerIntegrationTest extends BaseIntegrationTest{

    @Test
    void should_list_all_members() {
        // given
        givenGymExists();
        givenMembershipPlanExists();
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
    void should_register_new_member() {
        // given
        givenGymExists();
        givenMembershipPlanExists();
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
        givenMembershipPlanExists();
        givenMemberExists();

        // when & then
        testClient.patch()
                .uri("/api/members/1/cancel")
                .exchange()
                .expectStatus().isNoContent();
    }
}
