package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        String userName,
        String email,
        String codeforcesHandle,
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        boolean isCoach
) {}
