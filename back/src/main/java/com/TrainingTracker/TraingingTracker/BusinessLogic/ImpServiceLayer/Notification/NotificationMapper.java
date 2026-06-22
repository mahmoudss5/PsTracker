package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Notification;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Notification;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.NotificationType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationCreateDto dto, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Notification.builder()
                .user(user)
                .title(dto.title())
                .message(dto.message())
                .type(toNotificationType(dto.type()))
                .isRead(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public NotificationResponseDto toDto(Notification notification) {
        Long userId = notification.getUser() == null ? null : notification.getUser().getId();
        return new NotificationResponseDto(
                notification.getId(),
                userId,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType() == null ? null : notification.getType().name(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    private NotificationType toNotificationType(String type) {
        if (type == null || type.isBlank()) {
            throw new RuntimeException("Notification type is required");
        }
        return NotificationType.valueOf(type.trim().toUpperCase(Locale.ROOT));
    }
}
