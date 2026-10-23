package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpDto(
                @Size(min = 5, message = "user name must be at least 5") String userName,

                @Email String email,

                @NotBlank String codeforcesHandle,

                @Size(min = 8, message = "Password must be at least 8 characters long") String password,

                boolean isCoach) {
}
