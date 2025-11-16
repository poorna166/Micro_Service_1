package com.techcommerce.payment_service.service;

import com.techcommerce.payment_service.dto.PaymentDTO;
import com.techcommerce.payment_service.dto.PaymentRequestDTO;
import com.techcommerce.payment_service.entity.Payment;
import com.techcommerce.payment_service.event.PaymentProcessedEvent;
import com.techcommerce.payment_service.event.PaymentRefundedEvent;
import com.techcommerce.payment_service.exception.PaymentNotFoundException;
import com.techcommerce.payment_service.exception.PaymentProcessingException;
import com.techcommerce.payment_service.mapper.PaymentMapper;
import com.techcommerce.payment_service.publisher.PaymentEventPublisher;
import com.techcommerce.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing payment operations
 * Handles payment processing, status tracking, and refunds
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProcessor paymentProcessor;
    private final PaymentEventPublisher eventPublisher;

    /**
     * Process payment for an order
     */
    public PaymentDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        try {
            // Idempotency: if client provided an idempotency key and a payment exists, return it
            String idemKey = request.getIdempotencyKey();
            if (idemKey != null && !idemKey.isBlank()) {
                Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idemKey);
                if (existing.isPresent()) {
                    log.info("Found existing payment for idempotency key: {}", idemKey);
                    return paymentMapper.toDTO(existing.get());
                }
            }

            // Generate transaction ID
            String transactionId = generateTransactionId();

            // Process payment (mock implementation)
            boolean paymentSuccessful = paymentProcessor.processPayment(
                    transactionId,
                    request.getAmount(),
                    request.getPaymentMethod(),
                    request.getCardToken()
            );

            if (!paymentSuccessful) {
                log.error("Payment processing failed for order: {}", request.getOrderId());
                throw new PaymentProcessingException("Payment processing failed");
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setTransactionId(transactionId);
            payment.setAmount(request.getAmount());
            payment.setPaymentGateway(request.getPaymentMethod());
            payment.setStatus("COMPLETED");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setIdempotencyKey(idemKey);

            Payment saved = paymentRepository.save(payment);
            log.info("Payment processed successfully for order: {} with transaction ID: {}", 
                    request.getOrderId(), transactionId);

            // Publish payment processed event
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .paymentId(saved.getId())
                    .orderId(saved.getOrderId())
                    .transactionId(saved.getTransactionId())
                    .amount(saved.getAmount())
                    .status(saved.getStatus())
                    .timestamp(LocalDateTime.now())
                    .build();
            eventPublisher.publishPaymentProcessed(event);

            return paymentMapper.toDTO(saved);
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", request.getOrderId(), e);
            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment details by ID
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPayment(Long paymentId) {
        log.info("Fetching payment: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return paymentMapper.toDTO(payment);
    }

    /**
     * Get payment by order ID
     */
    // @Transactional(readOnly = true)
    // public PaymentDTO getPaymentByOrderId(Long orderId) {
    //     log.info("Fetching payment for order: {}", orderId);
    //     Payment payment = paymentRepository.findByOrderId(orderId)
    //             .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
    //     return paymentMapper.toDTO(payment);
    // }

    /**
     * Refund a payment
     */
    // public PaymentDTO refundPayment(Long paymentId) {
    //     log.info("Processing refund for payment: {}", paymentId);
        
    //     Payment payment = paymentRepository.findById(paymentId)
    //             .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

    //     if ("REFUNDED".equals(payment.getStatus())) {
    //         throw new PaymentProcessingException("Payment is already refunded");
    //     }

    //     try {
    //         // Process refund with payment processor
    //         boolean refundSuccessful = paymentProcessor.refundPayment(
    //                 payment.getTransactionId(),
    //                 payment.getAmount()
    //         );

    //         if (!refundSuccessful) {
    //             log.error("Refund processing failed for payment: {}", paymentId);
    //             throw new PaymentProcessingException("Refund processing failed");
    //         }

    //         payment.setStatus("REFUNDED");
    //         Payment refunded = paymentRepository.save(payment);
    //         log.info("Payment refunded successfully: {}", paymentId);
            
    //         // Publish payment refunded event
    //         PaymentRefundedEvent event = PaymentRefundedEvent.builder()
    //                 .paymentId(refunded.getId())
    //                 .orderId(refunded.getOrderId())
    //                 .transactionId(refunded.getTransactionId())
    //                 .amount(refunded.getAmount())
    //                 .reason("Customer refund request")
    //                 .timestamp(LocalDateTime.now())
    //                 .build();
    //         eventPublisher.publishPaymentRefunded(event);
            
    //         return paymentMapper.toDTO(refunded);
    //     } catch (Exception e) {
    //         log.error("Error refunding payment: {}", paymentId, e);
    //         throw new PaymentProcessingException("Failed to refund payment: " + e.getMessage(), e);
    //     }
    // }

    /**
     * Get all payments for an order range
     */
    // @Transactional(readOnly = true)
    // public List<PaymentDTO> getAllPayments() {
    //     log.info("Fetching all payments");
    //     return paymentRepository.findAll().stream()
    //             .map(paymentMapper::toDTO)
    //             .collect(Collectors.toList());
    // }

    /**
     * Get payments by status
     */
    // @Transactional(readOnly = true)
    // public List<PaymentDTO> getPaymentsByStatus(String status) {
    //     log.info("Fetching payments with status: {}", status);
    //     return paymentRepository.findByStatus(status).stream()
    //             .map(paymentMapper::toDTO)
    //             .collect(Collectors.toList());
    // }

    /**
     * Get payment by transaction ID
     */
    // @Transactional(readOnly = true)
    // public PaymentDTO getPaymentByTransactionId(String transactionId) {
    //     log.info("Fetching payment by transaction ID: {}", transactionId);
    //     Payment payment = paymentRepository.findByTransactionId(transactionId)
    //             .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction ID: " + transactionId));
    //     return paymentMapper.toDTO(payment);
    // }

    /**
     * Generate unique transaction ID
     */
//     private String generateTransactionId() {
//         return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
//     }
// }

    /**
     * Get payment details by ID
     */
    // @Transactional(readOnly = true)
    // public PaymentDTO getPayment(Long paymentId) {
    //     log.info("Fetching payment: {}", paymentId);
    //     Payment payment = paymentRepository.findById(paymentId)
    //             .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
    //     return paymentMapper.toDTO(payment);
    // }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for order: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        return paymentMapper.toDTO(payment);
    }

    /**
     * Refund a payment
     */
    public PaymentDTO refundPayment(Long paymentId) {
        log.info("Processing refund for payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        if ("REFUNDED".equals(payment.getStatus())) {
            throw new PaymentProcessingException("Payment is already refunded");
        }

        try {
            // Process refund with payment processor
            boolean refundSuccessful = paymentProcessor.refundPayment(
                    payment.getTransactionId(),
                    payment.getAmount()
            );

            if (!refundSuccessful) {
                log.error("Refund processing failed for payment: {}", paymentId);
                throw new PaymentProcessingException("Refund processing failed");
            }

            payment.setStatus("REFUNDED");
            Payment refunded = paymentRepository.save(payment);
            log.info("Payment refunded successfully: {}", paymentId);
            
            return paymentMapper.toDTO(refunded);
        } catch (Exception e) {
            log.error("Error refunding payment: {}", paymentId, e);
            throw new PaymentProcessingException("Failed to refund payment: " + e.getMessage(), e);
        }
    }

    /**
     * Get all payments for an order range
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payment by transaction ID
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment by transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction ID: " + transactionId));
        return paymentMapper.toDTO(payment);
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
