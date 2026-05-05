package com.task.gymmanagment.domain;

public class MembershipPlanExceedLimitException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Exceeded maximum members: %d for a given Membership plan with ID: %d";

    public MembershipPlanExceedLimitException(int maxMembers, long membershipId) {
        super(String.format(DEFAULT_MESSAGE, maxMembers, membershipId));
    }
}
