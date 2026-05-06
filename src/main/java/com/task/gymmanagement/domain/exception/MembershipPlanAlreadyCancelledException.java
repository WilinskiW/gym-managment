package com.task.gymmanagement.domain.exception;

public class MembershipPlanAlreadyCancelledException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Member with ID: %d is already cancelled";

    public MembershipPlanAlreadyCancelledException(Long memberId) {
        super(String.format(DEFAULT_MESSAGE, memberId));
    }
}
