package com.techcommerce.order_service.listener;

import com.techcommerce.order_service.dto.OrderDTO;
import com.techcommerce.order_service.event.OrderConfirmedEvent;
import com.techcommerce.order_service.publisher.OrderEventPublisher;
import com.techcommerce.order_service.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Listener for payment-related events
 * Processes PaymentProcessedEvent from Payment Service to update order status
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderService orderService;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * Listen for PaymentProcessedEvent from payment.processed queue
     * When payment is successful, update order status to CONFIRMED
     * Then publish OrderConfirmedEvent for other services (e.g., Notification Service)
     *
     * @param paymentEvent the payment processed event received from payment service
     */
    @RabbitListener(queues = "payment.processed.queue")
    public void handlePaymentProcessed(PaymentProcessedEvent paymentEvent) {
        try {
            log.info("Received PaymentProcessedEvent - Order ID: {}, Payment ID: {}", 
                    paymentEvent.getOrderId(), paymentEvent.getPaymentId());

            // Get the order and update its status to CONFIRMED
            OrderDTO order = orderService.updateOrderStatus(
                    paymentEvent.getOrderId(), 
                    "CONFIRMED"
            );

            log.info("Order status updated to CONFIRMED - Order ID: {}", paymentEvent.getOrderId());

            // Create and publish OrderConfirmedEvent for downstream services (Notification, etc.)
            OrderConfirmedEvent confirmEvent = OrderConfirmedEvent.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .totalAmount(order.getTotalAmount())
                    .items(order.getOrderItems().stream()
                            .map(item -> OrderConfirmedEvent.OrderItem.builder()
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build())
                            .toList())
                    .confirmedAt(LocalDateTime.now())
                    .build();

            orderEventPublisher.publishOrderConfirmed(confirmEvent);
            log.info("OrderConfirmedEvent published successfully - Order ID: {}", paymentEvent.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process PaymentProcessedEvent for order {}: {}", 
                    paymentEvent.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process payment event", e);
        }
    }
}

/**
 * DTO class for PaymentProcessedEvent
 * This matches the event structure from payment-service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class PaymentProcessedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long paymentId;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime timestamp;
}
