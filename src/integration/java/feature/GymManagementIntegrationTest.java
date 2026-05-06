package feature;

import com.task.gymmanagement.GymManagementApplication;
import com.task.gymmanagement.domain.dto.response.GymDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GymManagementApplication.class)
@ActiveProfiles("integration")
@AutoConfigureRestTestClient
public class GymManagementIntegrationTest {
    @Autowired
    private RestTestClient testClient;

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
}
