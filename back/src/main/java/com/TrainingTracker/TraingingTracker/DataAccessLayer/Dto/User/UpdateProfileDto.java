package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileDto(
        @NotBlank String userName) {
}
