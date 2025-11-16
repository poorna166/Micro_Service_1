package com.techcommerce.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for processing payment requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String cardToken;
    private String description;
}
