package com.techcommerce.payment_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Payment processor component
 * Mock implementation for payment gateway integration
 */
@Component
@Slf4j
public class PaymentProcessor {

    /**
     * Process payment with payment gateway
     * Mock implementation - always succeeds
     */
    public boolean processPayment(String transactionId, BigDecimal amount, 
                                   String paymentMethod, String cardToken) {
        log.info("Processing payment - Transaction ID: {}, Amount: {}, Method: {}", 
                transactionId, amount, paymentMethod);
        
        // Mock payment processing - in production, call actual payment gateway
        // Examples: Stripe, PayPal, Square, etc.
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid payment amount: {}", amount);
            return false;
        }

        if (cardToken == null || cardToken.isEmpty()) {
            log.error("Invalid card token");
            return false;
        }

        // Simulate payment processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
            return false;
        }

        log.info("Payment processed successfully - Transaction ID: {}", transactionId);
        return true;
    }

    /**
     * Refund payment with payment gateway
     * Mock implementation - always succeeds
     */
    public boolean refundPayment(String transactionId, BigDecimal amount) {
        log.info("Processing refund - Transaction ID: {}, Amount: {}", transactionId, amount);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid refund amount: {}", amount);
            return false;
        }

        // Simulate refund processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Refund processing interrupted", e);
            return false;
        }

        log.info("Refund processed successfully - Transaction ID: {}", transactionId);
        return true;
    }
}
