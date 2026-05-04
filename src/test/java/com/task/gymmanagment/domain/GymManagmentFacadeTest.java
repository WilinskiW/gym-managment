package com.task.gymmanagment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        return new GymManagmentFacade(new GymManagmentService(new SimpleInMemoryGymRepository()));
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
        assertThatThrownBy(() ->  gymManagmentFacade.addGym(secondRequest))
                .isInstanceOf(GymAlreadyExistException.class)
                .hasMessage("Gym with name Test gym already exists");
    }

    private AddGymRequestDto createAddGymRequest(String name){
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
    void should_list_all_gyms(){
        // given
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 1"));
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 2"));
        gymManagmentFacade.addGym(createAddGymRequest("Test gym 3"));

        // when
        List<GymInfoDto> gyms = gymManagmentFacade.getAllGyms();

        // then
        assertThat(gyms).hasSize(3);
        assertThat(gyms.getFirst().name()).isEqualTo("Test gym 1");
        assertThat(gyms.get(1).name()).isEqualTo("Test gym 2");
        assertThat(gyms.getLast().name()).isEqualTo("Test gym 3");
    }
}
