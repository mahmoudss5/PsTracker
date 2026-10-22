package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth;

import jakarta.validation.constraints.Email;

public record SignInDto(
    @Email 
    String email,
     
    String password
) {}



