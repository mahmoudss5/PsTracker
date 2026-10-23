package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Team;

import jakarta.validation.constraints.NotBlank;

public record TeamCreateDto(
                @NotBlank String teamName) {
}
