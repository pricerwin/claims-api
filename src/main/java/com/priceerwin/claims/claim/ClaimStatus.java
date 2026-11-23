package com.priceerwin.claims.claim;

import java.util.Set;

/**
 * Lifecycle of a claim. Each status knows which statuses it may move to,
 * so the transition rules live in one place instead of being scattered
 * across if-statements in the service.
 */
public enum ClaimStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    DENIED,
    PAID;

    public Set<ClaimStatus> allowedNext() {
        return switch (this) {
            case SUBMITTED -> Set.of(UNDER_REVIEW);
            case UNDER_REVIEW -> Set.of(APPROVED, DENIED);
            case APPROVED -> Set.of(PAID);
            case DENIED, PAID -> Set.of();
        };
    }

    public boolean canMoveTo(ClaimStatus next) {
        return allowedNext().contains(next);
    }
}
