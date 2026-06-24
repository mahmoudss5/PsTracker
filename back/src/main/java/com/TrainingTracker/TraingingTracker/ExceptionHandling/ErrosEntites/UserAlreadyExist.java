package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

import org.springframework.http.HttpStatus;

public class UserAlreadyExist extends AppException {
    public UserAlreadyExist(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
