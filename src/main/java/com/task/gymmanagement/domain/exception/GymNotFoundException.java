package com.task.gymmanagement.domain.exception;

public class GymNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Gym with ID: %d not found";

    public GymNotFoundException(Long gymId) {
        super(String.format(DEFAULT_MESSAGE, gymId));
    }
}
