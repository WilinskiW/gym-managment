package com.task.gymmanagment.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
class GymManagmentService {
    private final GymRepository gymRepository;

    public Long createGym(AddGymRequestDto gymRequestDto) {
        var gymName = gymRequestDto.name().trim();

        if(gymRepository.existsByName(gymName)){
            log.warn("Gym with name {} already exists", gymName);
            throw new GymAlreadyExistException(gymName);
        }

        if(gymRequestDto.name().isBlank() || gymRequestDto.address().isBlank() || gymRequestDto.phoneNumber().isBlank()){
            throw new IllegalArgumentException("All fields are required");
        }

        Gym gym = mapDtoToGymEntity(gymRequestDto);

        Gym addedGym = gymRepository.save(gym);
        log.info("Gym with name {} added successfully", gymName);
        return addedGym.getId();
    }


    private Gym mapDtoToGymEntity(AddGymRequestDto gymRequestDto) {
        return Gym.builder()
                .name(gymRequestDto.name().trim())
                .address(gymRequestDto.address().trim())
                .phoneNumber(gymRequestDto.phoneNumber().trim())
                .build();
    }
}
