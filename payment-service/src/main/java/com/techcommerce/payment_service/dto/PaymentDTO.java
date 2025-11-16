package com.techcommerce.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String paymentGateway;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    // Getters and setters
}
