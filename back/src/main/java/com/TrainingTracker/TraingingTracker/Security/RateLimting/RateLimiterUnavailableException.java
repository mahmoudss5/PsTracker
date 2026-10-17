package com.TrainingTracker.TraingingTracker.Security.RateLimting;

public class RateLimiterUnavailableException extends RuntimeException {

    public RateLimiterUnavailableException(Throwable cause) {
        super("Rate limiter unavailable", cause);
    }
}
