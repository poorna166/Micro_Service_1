package com.techcommerce.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for notification service
 * Listens to events from order and payment services
 */
@Configuration
public class RabbitMQConfig {

    // Order events
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CONFIRMED_QUEUE = "order.confirmed.queue";
    public static final String ORDER_CONFIRMED_ROUTING_KEY = "order.confirmed";

    // Payment events
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_PROCESSED_QUEUE = "payment.processed.queue";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";

    // ==================== ORDER EXCHANGE ====================

    /**
     * Define order exchange
     */
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * Queue for order confirmed events
     */
    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue(ORDER_CONFIRMED_QUEUE, true);
    }

    /**
     * Bind order confirmed queue to exchange
     */
    @Bean
    public Binding orderConfirmedBinding(Queue orderConfirmedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderConfirmedQueue)
                .to(orderExchange)
                .with(ORDER_CONFIRMED_ROUTING_KEY);
    }

    // ==================== PAYMENT EXCHANGE ====================

    /**
     * Define payment exchange
     */
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE, true, false);
    }

    /**
     * Queue for payment processed events
     */
    @Bean
    public Queue paymentProcessedQueue() {
        return new Queue(PAYMENT_PROCESSED_QUEUE, true);
    }

    /**
     * Bind payment processed queue to exchange
     */
    @Bean
    public Binding paymentProcessedBinding(Queue paymentProcessedQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentProcessedQueue)
                .to(paymentExchange)
                .with(PAYMENT_PROCESSED_ROUTING_KEY);
    }
}
