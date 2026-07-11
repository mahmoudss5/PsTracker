package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Notification;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.NotificationService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Notification;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.NotificationRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate broker;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationCreateDto dto) {
        User user = userService.getUserById(dto.userId());
        Notification notification = notificationMapper.toEntity(dto, user);
        NotificationResponseDto responseDto = notificationMapper.toDto(notificationRepository.save(notification));
        
        try {
            broker.convertAndSendToUser(user.getEmail(), "/queue/notifications", responseDto);
            log.debug("Pushed notification to user {}", user.getEmail());
        } catch (Exception ex) {
            log.warn("Failed to push notification to user {}: {}", user.getEmail(), ex.getMessage());
        }
        
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationById(Long id) {
        return notificationMapper.toDto(getNotificationEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByUserId(Long userId) {
        userService.getUserById(userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getCurrentUserNotifications() {
        return getNotificationsByUserId(SecuiryUserUtil.getCurrntUserId());
    }

    @Override
    @Transactional
    public NotificationResponseDto markAsRead(Long id) {
        Notification notification = getNotificationEntityById(id);
        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = getNotificationEntityById(id);
        notificationRepository.delete(notification);
    }

    private Notification getNotificationEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }
}
