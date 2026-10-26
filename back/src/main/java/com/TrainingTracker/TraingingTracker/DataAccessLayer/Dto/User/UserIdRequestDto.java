package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User;

import jakarta.validation.constraints.NotNull;

public record UserIdRequestDto(
        @NotNull Long userId) {
}
