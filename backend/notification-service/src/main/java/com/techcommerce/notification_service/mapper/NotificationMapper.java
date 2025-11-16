package com.techcommerce.notification_service.mapper;

import com.techcommerce.notification_service.dto.NotificationDTO;
import com.techcommerce.notification_service.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Notification entity and DTO
 */
@Component
public class NotificationMapper {

    /**
     * Convert Notification entity to DTO
     */
    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .orderId(notification.getOrderId())
                .paymentId(notification.getPaymentId())
                .build();
    }

    /**
     * Convert DTO to Notification entity
     */
    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Notification.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .status(dto.getStatus())
                .isRead(dto.getIsRead())
                .createdAt(dto.getCreatedAt())
                .sentAt(dto.getSentAt())
                .orderId(dto.getOrderId())
                .paymentId(dto.getPaymentId())
                .build();
    }
}
