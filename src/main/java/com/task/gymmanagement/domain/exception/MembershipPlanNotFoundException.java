package com.task.gymmanagement.domain.exception;

public class MembershipPlanNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Membership plan with ID: %d not found";

    public MembershipPlanNotFoundException(Long id) {
        super(String.format(DEFAULT_MESSAGE, id));
    }
}
