package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

public class RateLimitExceededException extends RuntimeException {
    private final long retryAfterMs;

    public RateLimitExceededException(long retryAfterMs) {
        super("Rate limit exceeded");
        this.retryAfterMs = retryAfterMs;
    }

    public long getRetryAfterMs() {
        return retryAfterMs;
    }

}
