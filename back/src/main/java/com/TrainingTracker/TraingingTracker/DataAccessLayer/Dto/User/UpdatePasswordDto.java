package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

public record UpdatePasswordDto(
    String oldPassword,
    String newPassword
) {
}
