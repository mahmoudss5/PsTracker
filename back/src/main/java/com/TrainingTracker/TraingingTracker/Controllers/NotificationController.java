package com.TrainingTracker.TraingingTracker.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.NotificationService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationCreateDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Notification.NotificationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Create notification")
    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(@RequestBody NotificationCreateDto dto) {
        return ResponseEntity.ok(notificationService.createNotification(dto));
    }

    @Operation(summary = "Get all notifications")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @Operation(summary = "Get current user notifications")
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponseDto>> getCurrentUserNotifications() {
        return ResponseEntity.ok(notificationService.getCurrentUserNotifications());
    }

    @Operation(summary = "Get notifications by user id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @Operation(summary = "Get notification by id")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "Delete notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
