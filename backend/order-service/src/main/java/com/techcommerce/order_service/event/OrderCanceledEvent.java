package com.techcommerce.order_service.event;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCanceledEvent implements Serializable {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
}
