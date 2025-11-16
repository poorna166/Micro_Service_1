package com.techcommerce.inventory_service.listener;

import com.techcommerce.inventory_service.event.OrderPlacedEvent;
import com.techcommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ listener for handling inventory-related events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservationListener {

    private final InventoryService inventoryService;

    /**
     * Listen to order placed events and reserve inventory
     */
    @RabbitListener(queues = "inventory.queue")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent - Order ID: {}", event.getOrderId());
        
        try {
            // Reserve stock for each item in the order
            event.getItems().forEach(item -> {
                log.info("Reserving {} units of product {}", item.getQuantity(), item.getProductId());
                inventoryService.reserveStock(item.getProductId(), item.getQuantity());
            });
            
            log.info("Successfully reserved inventory for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error reserving inventory for order: {}", event.getOrderId(), e);
            // Could publish a compensation event here for order cancellation
            throw new RuntimeException("Failed to reserve inventory", e);
        }
    }

    /**
     * Listen to order canceled events and release inventory
     */
    @RabbitListener(queues = "inventory.released.queue")
    public void handleOrderCanceled(OrderPlacedEvent event) {
        log.info("Received OrderCanceledEvent - Order ID: {}", event.getOrderId());
        
        try {
            // Release reserved stock for each item in the order
            event.getItems().forEach(item -> {
                log.info("Releasing {} units of product {}", item.getQuantity(), item.getProductId());
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            });
            
            log.info("Successfully released inventory for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error releasing inventory for order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to release inventory", e);
        }
    }
}
