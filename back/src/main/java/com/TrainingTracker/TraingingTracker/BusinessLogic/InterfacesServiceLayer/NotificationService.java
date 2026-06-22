package com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;

import java.util.List;

public interface NotificationService {
    NotificationResponseDto createNotification(NotificationCreateDto dto);

    NotificationResponseDto getNotificationById(Long id);

    List<NotificationResponseDto> getAllNotifications();

    List<NotificationResponseDto> getNotificationsByUserId(Long userId);

    List<NotificationResponseDto> getCurrentUserNotifications();

    NotificationResponseDto markAsRead(Long id);

    void deleteNotification(Long id);
}
