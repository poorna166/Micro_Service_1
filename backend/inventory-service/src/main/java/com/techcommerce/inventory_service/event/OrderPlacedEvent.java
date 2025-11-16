package com.techcommerce.inventory_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event published when an order is placed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent implements Serializable {
    
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
