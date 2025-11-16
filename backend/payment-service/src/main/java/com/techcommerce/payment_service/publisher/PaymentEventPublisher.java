package com.techcommerce.payment_service.publisher;

import com.techcommerce.payment_service.event.PaymentProcessedEvent;
import com.techcommerce.payment_service.event.PaymentRefundedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publisher for payment-related events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_EXCHANGE = "payment.exchange";
    private static final String PAYMENT_PROCESSED_KEY = "payment.processed";
    private static final String PAYMENT_REFUNDED_KEY = "payment.refunded";

    /**
     * Publish payment processed event
     */
    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Publishing PaymentProcessedEvent - Order ID: {}", event.getOrderId());
        rabbitTemplate.convertAndSend(
                PAYMENT_EXCHANGE,
                PAYMENT_PROCESSED_KEY,
                event
        );
        log.info("PaymentProcessedEvent published successfully");
    }

    /**
     * Publish payment refunded event
     */
    public void publishPaymentRefunded(PaymentRefundedEvent event) {
        log.info("Publishing PaymentRefundedEvent - Order ID: {}", event.getOrderId());
        rabbitTemplate.convertAndSend(
                PAYMENT_EXCHANGE,
                PAYMENT_REFUNDED_KEY,
                event
        );
        log.info("PaymentRefundedEvent published successfully");
    }
}
