package com.task.gymmanagement.domain.exception.member;

public class MemberNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Member with ID: %d not found";

    public MemberNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
