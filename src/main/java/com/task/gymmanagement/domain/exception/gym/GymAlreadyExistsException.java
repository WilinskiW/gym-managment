package com.task.gymmanagement.domain.exception.gym;

public class GymAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Gym with name %s already exists";

    public GymAlreadyExistsException(String gymName) {
        super(String.format(DEFAULT_MESSAGE, gymName));
    }
}
