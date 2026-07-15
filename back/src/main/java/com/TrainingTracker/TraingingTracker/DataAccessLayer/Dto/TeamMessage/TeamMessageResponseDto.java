package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage;

import java.time.OffsetDateTime;

public record TeamMessageResponseDto(
        Long id,
        String content,
        OffsetDateTime createdAt,
        Long teamId,
        Long senderId,
        String senderUsername) {
}
