package com.TrainingTracker.TraingingTracker.Security.RateLimting;

public record RateLimitDecision(
        boolean isAllowed,
        long remainingRequests,
        long retryAfterMs
) {
}
