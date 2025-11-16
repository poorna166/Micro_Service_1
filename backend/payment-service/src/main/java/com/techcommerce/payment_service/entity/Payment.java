package com.techcommerce.payment_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transaction_id"}),
    @UniqueConstraint(columnNames = {"idempotency_key"})
})
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_gateway", length = 20, nullable = false)
    private String paymentGateway;

    @Column(name = "transaction_id", length = 150, nullable = false)
    private String transactionId;

    @Column(name = "idempotency_key", length = 100, unique = true)
    private String idempotencyKey;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and setters
}