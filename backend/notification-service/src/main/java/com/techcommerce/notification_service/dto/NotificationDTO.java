package com.techcommerce.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Notification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String message;
    private String status;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private Long orderId;
    private Long paymentId;
}
