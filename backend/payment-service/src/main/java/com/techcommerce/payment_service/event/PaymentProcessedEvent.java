package com.techcommerce.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when payment is processed successfully
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long paymentId;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;
}
