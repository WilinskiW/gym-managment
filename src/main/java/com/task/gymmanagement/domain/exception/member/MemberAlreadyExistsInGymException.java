package com.task.gymmanagement.domain.exception.member;

public class MemberAlreadyExistsInGymException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Member with email and active plan: %s already exists in gym: %s (ID: %d)";

    public MemberAlreadyExistsInGymException(String email, String name, Long gymId) {
        super(String.format(DEFAULT_MESSAGE, email, name, gymId));
    }
}
