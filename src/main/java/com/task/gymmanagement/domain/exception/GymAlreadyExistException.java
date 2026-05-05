package com.task.gymmanagement.domain.exception;

public class GymAlreadyExistException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Gym with name %s already exists";

    public GymAlreadyExistException(String gymName) {
        super(String.format(DEFAULT_MESSAGE, gymName));
    }
}
