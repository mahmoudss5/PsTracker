package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth;

public record SignUpDto(
        String userName,
        String email,
        String password,
        boolean isCoach
) {}
