package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

public class CfQueueException extends RuntimeException {

    private final String errorType;

    public CfQueueException(String errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public String errorType() {
        return errorType;
    }
}
