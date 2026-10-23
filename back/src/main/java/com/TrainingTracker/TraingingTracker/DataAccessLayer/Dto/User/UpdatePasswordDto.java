package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDto(
        @NotBlank String oldPassword,

        @NotBlank String newPassword) {
}
