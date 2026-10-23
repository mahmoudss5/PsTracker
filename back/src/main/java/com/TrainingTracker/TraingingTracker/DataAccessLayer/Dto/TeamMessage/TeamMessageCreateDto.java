package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage;

import jakarta.validation.constraints.NotBlank;

public record TeamMessageCreateDto(@NotBlank String content) {
}
