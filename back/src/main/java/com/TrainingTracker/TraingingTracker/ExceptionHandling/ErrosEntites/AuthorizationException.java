package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends AppException {

    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
