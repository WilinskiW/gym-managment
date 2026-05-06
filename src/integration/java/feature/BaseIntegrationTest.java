package feature;

import com.task.gymmanagement.GymManagementApplication;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GymManagementApplication.class)
@ActiveProfiles("integration")
@AutoConfigureRestTestClient
public abstract class BaseIntegrationTest {
    @Autowired
    protected RestTestClient testClient;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    void givenGymExists() {
        givenGymExists("Power gym", "Street 1");
    }

    void givenGymExists(String name, String address) {
        String request = """
                {
                    "name": "%s",
                    "address": "%s",
                    "phoneNumber": "+48 111 222 333"
                }
                """.formatted(name, address);

        testClient.post().uri("/api/gyms").contentType(MediaType.APPLICATION_JSON).body(request).exchange();
    }

    void givenMembershipPlanExists() {
        givenMembershipPlanExists(1L, "Default Plan", 100, "PLN", 10);
    }

    void givenMembershipPlanExists(Long gymId, String name, int amount, String currency) {
        givenMembershipPlanExists(gymId, name, amount, currency, 10);
    }

    void givenMembershipPlanExists(Long gymId, String name, int amount, String currency, int maxMembers) {
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

    void givenMemberExists() {
        givenMemberExists(1L, "Default Member", "default@test.com");
    }

    void givenMemberExists(Long planId, String name, String email) {
        String request = """
                {
                    "membershipId": %d,
                    "fullName": "%s",
                    "email": "%s"
                }
                """.formatted(planId, name, email);

        testClient.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON).body(request).exchange();
    }
}
