package com.task.gymmanagment.domain.exception;

public class GymNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Gym with name %s not found";

    public GymNotFoundException(String gymName) {
        super(String.format(DEFAULT_MESSAGE, gymName));
    }
}
