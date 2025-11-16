package com.techcommerce.notification_service.service;

import com.techcommerce.notification_service.dto.EmailRequest;
import com.techcommerce.notification_service.dto.NotificationDTO;
import com.techcommerce.notification_service.dto.SmsRequest;
import com.techcommerce.notification_service.mapper.NotificationMapper;
import com.techcommerce.notification_service.model.Notification;
import com.techcommerce.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing notifications
 * Handles sending, storing, and retrieving notifications
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;

    /**
     * Send and store notification
     */
    public NotificationDTO sendNotification(Long userId, String type, String title, 
                                            String message, Long orderId, Long paymentId) {
        log.info("Sending notification to user: {} - Type: {}", userId, type);
        
        try {
            // Create notification record
            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .message(message)
                    .status("PENDING")
                    .isRead(false)
                    .orderId(orderId)
                    .paymentId(paymentId)
                    .build();

            // Send based on type
            if ("EMAIL".equals(type)) {
                emailService.sendEmail(userId, title, message);
            } else if ("SMS".equals(type)) {
                log.info("SMS sending not yet implemented");
            }

            // Update status to sent
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            
            Notification saved = notificationRepository.save(notification);
            log.info("Notification sent successfully to user: {}", userId);
            
            return notificationMapper.toDTO(saved);
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", userId, e);
            
            // Save failed notification
            Notification failedNotification = Notification.builder()
                    .userId(userId)
                    .type(type)
                    .title(title)
                    .message(message)
                    .status("FAILED")
                    .isRead(false)
                    .failureReason(e.getMessage())
                    .orderId(orderId)
                    .paymentId(paymentId)
                    .build();
            
            Notification saved = notificationRepository.save(failedNotification);
            return notificationMapper.toDTO(saved);
        }
    }

    /**
     * Get all notifications for a user (paginated)
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, int page, int size) {
        log.info("Fetching notifications for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toDTO);
    }

    /**
     * Get unread notifications for a user
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository.findByUserIdAndIsReadFalse(userId)
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark notification as read
     */
    public NotificationDTO markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        
        return notificationMapper.toDTO(updated);
    }

    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationDTO getNotification(Long notificationId) {
        log.info("Fetching notification: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return notificationMapper.toDTO(notification);
    }

    /**
     * Get notifications for an order
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByOrderId(Long orderId) {
        log.info("Fetching notifications for order: {}", orderId);
        return notificationRepository.findByOrderId(orderId)
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Count unread notifications for a user
     */
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long userId) {
        log.info("Counting unread notifications for user: {}", userId);
        return (long) notificationRepository.findByUserIdAndIsReadFalse(userId).size();
    }
}
