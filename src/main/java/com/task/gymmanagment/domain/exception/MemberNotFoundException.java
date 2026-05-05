package com.task.gymmanagment.domain.exception;

public class MemberNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Member with ID: %d not found";

    public MemberNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
