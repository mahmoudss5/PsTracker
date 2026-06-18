package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth;

public record AuthResponse(String token,
                           String RefershToken,
                           Long userId,
                           String userName,
                           boolean isCoach) {}
