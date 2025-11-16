package com.techcommerce.notification_service.listener;

import com.techcommerce.notification_service.event.PaymentProcessedEvent;
import com.techcommerce.notification_service.service.EmailService;
import com.techcommerce.notification_service.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Listener for payment-related events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final NotificationServiceImpl notificationService;
    private final EmailService emailService;

    /**
     * Listen to payment processed events and send receipt email
     */
    @RabbitListener(queues = "payment.processed.queue")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received PaymentProcessedEvent - Transaction ID: {}", event.getTransactionId());
        
        try {
            // Send payment receipt email
            emailService.sendPaymentReceiptEmail(
                    event.getOrderId(), // Use orderId as userId placeholder
                    event.getTransactionId(),
                    event.getAmount().toString()
            );
            
            // Store notification in database
            notificationService.sendNotification(
                    event.getOrderId(), // Use orderId as userId placeholder
                    "EMAIL",
                    "Payment Received",
                    "Payment of " + event.getAmount() + " has been processed. Transaction ID: " + event.getTransactionId(),
                    event.getOrderId(),
                    event.getPaymentId()
            );
            
            log.info("Payment receipt email sent for transaction: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Error handling payment processed event for transaction: {}", event.getTransactionId(), e);
        }
    }
}
