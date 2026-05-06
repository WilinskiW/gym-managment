package feature;

import com.task.gymmanagement.domain.MemberStatus;
import com.task.gymmanagement.domain.dto.response.MemberDto;
import com.task.gymmanagement.infrastructure.error.ErrorResponseDto;
import com.task.gymmanagement.infrastructure.error.ValidationErrorResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should list all members")
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
                .expectBody(new ParameterizedTypeReference<List<MemberDto>>() {
                })
                .value(members -> {
                    assertThat(members).hasSize(2);
                    assertThat(members).extracting(MemberDto::name)
                            .containsExactlyInAnyOrder("Member One", "Member Two");
                });
    }

    @Test
    @DisplayName("Should return error when validation failed while adding member")
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
    @DisplayName("Should register new member")
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
    @DisplayName("Should return error when trying to add member when already exists in gym")
    void should_return_error_when_trying_to_add_member_when_already_exists_in_gym(){
        // given
        givenGymExists();
        givenMembershipPlanExists();
        givenMemberExists();

        var request = """
                {
                    "membershipId": 1,
                    "fullName": "Jan Kowalski",
                    "email": "default@test.com"
                }
                """;

        testClient.post().uri("/api/members").body(request).exchange();

        // when & then
        testClient.post()
                .uri("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(new ErrorResponseDto(HttpStatus.CONFLICT.value(),
                        "Member with email and active plan: default@test.com already exists in gym: Power gym (ID: 1)"));
    }

    @Test
    @DisplayName("Should cancel membership")
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

    @Test
    @DisplayName("Should return error when trying to cancelled member who was cancelled before")
    void should_return_error_when_trying_to_cancelled_member_who_was_cancelled_before() {
        //given
        givenGymExists();
        givenMembershipPlanExists();
        givenMemberExists();

        testClient.patch().uri("/api/members/1/cancel").exchange();

        // when & then
        testClient.patch().uri("/api/members/1/cancel")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponseDto.class)
                .value(body -> assertThat(body)
                        .isEqualTo(new ErrorResponseDto(HttpStatus.CONFLICT.value(),
                                "Member with ID: 1 is already cancelled")
                        )
                );

    }

    @Test
    @DisplayName("Should return error when trying to cancel non-existing member")
    void should_return_error_when_trying_to_cancel_non_existing_member() {
        // when & then
        testClient.patch().uri("/api/members/1/cancel")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(body -> assertThat(body)
                        .isEqualTo(new ErrorResponseDto(HttpStatus.NOT_FOUND.value(),
                                "Member with ID: 1 not found")
                        )
                );
    }
}
