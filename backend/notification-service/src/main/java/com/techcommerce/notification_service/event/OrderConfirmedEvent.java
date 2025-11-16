package com.techcommerce.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when order is confirmed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long orderId;
    private Long userId;
    private List<OrderItem> items;
    private Double totalAmount;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private Long productId;
        private Integer quantity;
        private Double price;
    }
}
