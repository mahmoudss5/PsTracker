package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification;

public record NotificationCreateDto(
        Long userId,
        String title,
        String message,
        String type
) {
}
