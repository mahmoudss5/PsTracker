package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        Long id,
        Long userId,
        String title,
        String message,
        String type,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
