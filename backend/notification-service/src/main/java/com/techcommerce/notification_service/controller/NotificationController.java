package com.techcommerce.notification_service.controller;

import com.techcommerce.notification_service.dto.NotificationDTO;
import com.techcommerce.notification_service.service.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for notification management
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Service", description = "Manage user notifications")
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    /**
     * Get notifications for a user (paginated)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationDTO>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/notifications/user/{} - Page: {}, Size: {}", userId, page, size);
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable Long userId) {
        log.info("GET /api/notifications/user/{}/unread", userId);
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get single notification
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long notificationId) {
        log.info("GET /api/notifications/{}", notificationId);
        NotificationDTO notification = notificationService.getNotification(notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long notificationId) {
        log.info("PUT /api/notifications/{}/read", notificationId);
        NotificationDTO updated = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        log.info("PUT /api/notifications/user/{}/read-all", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        log.info("DELETE /api/notifications/{}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get notifications for an order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByOrderId(@PathVariable Long orderId) {
        log.info("GET /api/notifications/order/{}", orderId);
        List<NotificationDTO> notifications = notificationService.getNotificationsByOrderId(orderId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Count unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        log.info("GET /api/notifications/user/{}/unread-count", userId);
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }
}
