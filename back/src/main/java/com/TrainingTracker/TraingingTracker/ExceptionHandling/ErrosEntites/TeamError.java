package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

public class TeamError extends RuntimeException {
    public TeamError(String message) {
        super(message);
    }
}
