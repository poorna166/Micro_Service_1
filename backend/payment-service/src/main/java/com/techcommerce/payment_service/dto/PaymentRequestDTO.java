package com.techcommerce.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for processing payment requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    private String cardToken;

    private String description;

    // Optional idempotency key provided by the client (Idempotency-Key header or body)
    private String idempotencyKey;
}
