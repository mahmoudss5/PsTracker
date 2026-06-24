package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Notification;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Notification;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.NotificationRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private ImpNotificationService notificationService;

    @Test
    void testCreateNotification() {
        NotificationCreateDto createDto = new NotificationCreateDto(1L, "Title", "Msg", "INFO");
        User user = User.builder().id(1L).build();
        Notification notification = Notification.builder().id(10L).title("Title").build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                10L, 1L, "Title", "Msg", "INFO", false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userService.getUserById(1L)).thenReturn(user);
        when(notificationMapper.toEntity(createDto, user)).thenReturn(notification);
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(responseDto);

        NotificationResponseDto actual = notificationService.createNotification(createDto);

        assertNotNull(actual);
        assertEquals(10L, actual.id());
        verify(userService).getUserById(1L);
        verify(notificationMapper).toEntity(createDto, user);
        verify(notificationRepository).save(notification);
        verify(notificationMapper).toDto(notification);
    }

    @Test
    void testGetNotificationById_Success() {
        Long id = 10L;
        Notification notification = Notification.builder().id(id).title("Title").build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                id, 1L, "Title", "Msg", "INFO", false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(responseDto);

        NotificationResponseDto actual = notificationService.getNotificationById(id);

        assertNotNull(actual);
        assertEquals(id, actual.id());
    }

    @Test
    void testGetNotificationById_NotFound() {
        Long id = 999L;
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> notificationService.getNotificationById(id));
        assertEquals("Notification not found with id: " + id, exception.getMessage());
    }

    @Test
    void testGetAllNotifications() {
        Notification notif = Notification.builder().id(10L).build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                10L, 1L, "Title", "Msg", "INFO", false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationRepository.findAll()).thenReturn(List.of(notif));
        when(notificationMapper.toDto(notif)).thenReturn(responseDto);

        List<NotificationResponseDto> results = notificationService.getAllNotifications();

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
    }

    @Test
    void testGetNotificationsByUserId() {
        Long userId = 1L;
        Notification notif = Notification.builder().id(10L).build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                10L, userId, "Title", "Msg", "INFO", false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userService.getUserById(userId)).thenReturn(User.builder().id(userId).build());
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notif));
        when(notificationMapper.toDto(notif)).thenReturn(responseDto);

        List<NotificationResponseDto> results = notificationService.getNotificationsByUserId(userId);

        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).id());
    }

    @Test
    void testGetCurrentUserNotifications() {
        Long userId = 42L;
        Notification notif = Notification.builder().id(10L).build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                10L, userId, "Title", "Msg", "INFO", false, LocalDateTime.now(), LocalDateTime.now()
        );

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(userId);
            when(userService.getUserById(userId)).thenReturn(User.builder().id(userId).build());
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notif));
            when(notificationMapper.toDto(notif)).thenReturn(responseDto);

            List<NotificationResponseDto> results = notificationService.getCurrentUserNotifications();

            assertEquals(1, results.size());
            assertEquals(10L, results.get(0).id());
        }
    }

    @Test
    void testMarkAsRead() {
        Long id = 10L;
        Notification notification = Notification.builder().id(id).isRead(false).build();
        NotificationResponseDto responseDto = new NotificationResponseDto(
                id, 1L, "Title", "Msg", "INFO", true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(responseDto);

        NotificationResponseDto actual = notificationService.markAsRead(id);

        assertNotNull(actual);
        assertTrue(actual.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void testDeleteNotification() {
        Long id = 10L;
        Notification notification = Notification.builder().id(id).build();

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(id);

        verify(notificationRepository).delete(notification);
    }
}
