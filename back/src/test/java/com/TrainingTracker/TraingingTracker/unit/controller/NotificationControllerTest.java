package com.TrainingTracker.TraingingTracker.unit.controller;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.NotificationService;
import com.TrainingTracker.TraingingTracker.Controllers.NotificationController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private void authenticate(Long userId) {
        User user = User.builder().id(userId).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new SecurityUser(user), null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private NotificationResponseDto responseDto(boolean read) {
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 10, 12, 0);
        return new NotificationResponseDto(1L, 10L, "Practice", "New assignment", "INFO", read, createdAt, createdAt);
    }

    @Test
    void testCreateNotificationUsesAuthenticatedUser() {
        Long userId = 10L;
        authenticate(userId);
        NotificationCreateDto request = new NotificationCreateDto(99L, "Practice", "New assignment", "INFO");
        NotificationCreateDto expected = new NotificationCreateDto(userId, "Practice", "New assignment", "INFO");
        NotificationResponseDto dto = responseDto(false);
        when(notificationService.createNotification(expected)).thenReturn(dto);

        ResponseEntity<NotificationResponseDto> response = notificationController.createNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(notificationService).createNotification(expected);
    }

    @Test
    void testGetAllNotifications() {
        List<NotificationResponseDto> dtoList = List.of(responseDto(false));
        when(notificationService.getAllNotifications()).thenReturn(dtoList);

        ResponseEntity<List<NotificationResponseDto>> response = notificationController.getAllNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(notificationService).getAllNotifications();
    }

    @Test
    void testGetCurrentUserNotifications() {
        List<NotificationResponseDto> dtoList = List.of(responseDto(false));
        when(notificationService.getCurrentUserNotifications()).thenReturn(dtoList);

        ResponseEntity<List<NotificationResponseDto>> response = notificationController.getCurrentUserNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(notificationService).getCurrentUserNotifications();
    }

    @Test
    void testGetNotificationsByUserId() {
        Long userId = 10L;
        UserIdRequestDto request = new UserIdRequestDto(userId);
        List<NotificationResponseDto> dtoList = List.of(responseDto(false));
        when(notificationService.getNotificationsByUserId(userId)).thenReturn(dtoList);

        ResponseEntity<List<NotificationResponseDto>> response = notificationController.getNotificationsByUserId(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(notificationService).getNotificationsByUserId(userId);
    }

    @Test
    void testGetNotificationById() {
        Long notificationId = 1L;
        NotificationResponseDto dto = responseDto(false);
        when(notificationService.getNotificationById(notificationId)).thenReturn(dto);

        ResponseEntity<NotificationResponseDto> response = notificationController.getNotificationById(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(notificationService).getNotificationById(notificationId);
    }

    @Test
    void testMarkAsRead() {
        Long notificationId = 1L;
        NotificationResponseDto dto = responseDto(true);
        when(notificationService.markAsRead(notificationId)).thenReturn(dto);

        ResponseEntity<NotificationResponseDto> response = notificationController.markAsRead(notificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(notificationService).markAsRead(notificationId);
    }

    @Test
    void testDeleteNotification() {
        Long notificationId = 1L;

        ResponseEntity<Void> response = notificationController.deleteNotification(notificationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(notificationService).deleteNotification(notificationId);
    }
}
