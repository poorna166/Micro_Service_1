package com.techcommerce.notification_service.listener;

import com.techcommerce.notification_service.event.OrderConfirmedEvent;
import com.techcommerce.notification_service.service.EmailService;
import com.techcommerce.notification_service.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Listener for order-related events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final NotificationServiceImpl notificationService;
    private final EmailService emailService;

    /**
     * Listen to order confirmed events and send confirmation email
     */
    @RabbitListener(queues = "order.confirmed.queue")
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Received OrderConfirmedEvent - Order ID: {}", event.getOrderId());
        
        try {
            // Send order confirmation email
            String orderDetails = buildOrderDetails(event);
            emailService.sendOrderConfirmationEmail(event.getUserId(), event.getOrderId().toString(), orderDetails);
            
            // Store notification in database
            notificationService.sendNotification(
                    event.getUserId(),
                    "EMAIL",
                    "Order Confirmed",
                    "Your order #" + event.getOrderId() + " has been confirmed",
                    event.getOrderId(),
                    null
            );
            
            log.info("Order confirmation email sent for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error handling order confirmed event for order: {}", event.getOrderId(), e);
        }
    }

    /**
     * Build order details string for email
     */
    private String buildOrderDetails(OrderConfirmedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(event.getOrderId()).append("\n");
        sb.append("Order Date: ").append(LocalDateTime.now()).append("\n");
        sb.append("Total Amount: ").append(event.getTotalAmount()).append("\n");
        sb.append("Items:\n");
        if (event.getItems() != null) {
            event.getItems().forEach(item ->
                    sb.append("  - Product ").append(item.getProductId())
                            .append(" x").append(item.getQuantity())
                            .append(" @ ").append(item.getPrice()).append("\n")
            );
        }
        return sb.toString();
    }
}
