package com.techcommerce.inventory_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for inventory service
 * Handles event consumption from order service
 */
@Configuration
public class RabbitMQConfig {

    // Exchange and Queue names
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String INVENTORY_ROUTING_KEY = "order.placed";

    public static final String INVENTORY_RELEASED_QUEUE = "inventory.released.queue";
    public static final String INVENTORY_RELEASED_ROUTING_KEY = "order.canceled";

    /**
     * Define the order exchange
     */
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * Queue for order placed events
     */
    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_QUEUE, true);
    }

    /**
     * Queue for inventory release events
     */
    @Bean
    public Queue inventoryReleasedQueue() {
        return new Queue(INVENTORY_RELEASED_QUEUE, true);
    }

    /**
     * Bind inventory queue to exchange
     */
    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(inventoryQueue)
                .to(orderExchange)
                .with(INVENTORY_ROUTING_KEY);
    }

    /**
     * Bind inventory release queue to exchange
     */
    @Bean
    public Binding inventoryReleasedBinding(Queue inventoryReleasedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(inventoryReleasedQueue)
                .to(orderExchange)
                .with(INVENTORY_RELEASED_ROUTING_KEY);
    }
}
