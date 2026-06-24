package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.TeamMessage;

import java.time.LocalDateTime;

public record TeamMessageResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long teamId,
        Long senderId,
        String senderUsername) {
}
