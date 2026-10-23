package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Announcment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AnnouncmentCreateDto(
                @NotBlank String type,

                @NotBlank String content,

                @NotNull @Positive Long senderId,

                boolean isTeamAnnouncment,

                @NotNull @Positive Long receiverId) {
}
