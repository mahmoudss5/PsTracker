package com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites;

import org.springframework.http.HttpStatus;

public class CfUserHandlerError extends AppException {
    public CfUserHandlerError(String message) {

        super(message, HttpStatus.NOT_FOUND);
    }
}
