package com.techcommerce.payment_service.repository;

import com.techcommerce.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payment by order ID
     */
    Optional<Payment> findByOrderId(Long orderId);
    
    /**
     * Find all payments by status
     */
    List<Payment> findByStatus(String status);
    
    /**
     * Find payments by order ID and status
     */
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.status = :status")
    Optional<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") String status);

    /**
     * Find payment by client-provided idempotency key
     */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}