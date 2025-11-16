package com.techcommerce.payment_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for payment service
 * Publishes payment events for other services to consume
 */
@Configuration
public class RabbitMQConfig {

    // Exchange name
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    
    // Payment processed event
    public static final String PAYMENT_PROCESSED_QUEUE = "payment.processed.queue";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    
    // Payment refunded event
    public static final String PAYMENT_REFUNDED_QUEUE = "payment.refunded.queue";
    public static final String PAYMENT_REFUNDED_ROUTING_KEY = "payment.refunded";

    /**
     * Define the payment exchange
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
     * Queue for payment refunded events
     */
    @Bean
    public Queue paymentRefundedQueue() {
        return new Queue(PAYMENT_REFUNDED_QUEUE, true);
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

    /**
     * Bind payment refunded queue to exchange
     */
    @Bean
    public Binding paymentRefundedBinding(Queue paymentRefundedQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentRefundedQueue)
                .to(paymentExchange)
                .with(PAYMENT_REFUNDED_ROUTING_KEY);
    }
}
