package com.techcommerce.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Payment entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    
    private Long id;
    private Long orderId;
    private String paymentGateway;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
