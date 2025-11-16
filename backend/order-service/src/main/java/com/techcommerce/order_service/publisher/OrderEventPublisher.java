package com.techcommerce.order_service.publisher;

import com.techcommerce.order_service.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher for order-related events
 * Sends events to RabbitMQ exchanges for other services to consume
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String ORDER_EXCHANGE = "order.exchange";
    private static final String ORDER_CONFIRMED_ROUTING_KEY = "order.confirmed";

    /**
     * Publish OrderConfirmedEvent to RabbitMQ
     * This event is consumed by NotificationService to send order confirmation emails
     *
     * @param event the order confirmed event
     */
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        try {
            rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_CONFIRMED_ROUTING_KEY, event);
            log.info("OrderConfirmedEvent published successfully. Order ID: {}, User ID: {}", 
                    event.getOrderId(), event.getUserId());
        } catch (Exception e) {
            log.error("Failed to publish OrderConfirmedEvent for order {}: {}", 
                    event.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish order confirmed event", e);
        }
    }
}
