package com.task.gymmanagment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GymManagmentFacadeTest {
    private final GymManagmentFacade gymManagmentFacade = new GymManagmentFacade(new SimpleInMemoryGymRepository());

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
        var firstRequest = AddGymRequestDto.builder()
                .name("Test gym")
                .address("Test addres")
                .phoneNumber("123456789")
                .build();

        var secondRequest = AddGymRequestDto.builder()
                .name("    Test gym   ")
                .address("Test addres")
                .phoneNumber("123456789")
                .build();

        // when
        gymManagmentFacade.addGym(firstRequest);

        // then
        assertThatThrownBy(() ->  gymManagmentFacade.addGym(secondRequest))
                .isInstanceOf(GymAlreadyExistException.class)
                .hasMessage("Gym with name Test gym already exists");
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
}
