package com.techcommerce.inventory_service.controller;

import com.techcommerce.inventory_service.dto.InventoryDTO;
import com.techcommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for inventory management endpoints
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Get inventory details for a product
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        log.info("GET /api/inventory/{}", productId);
        InventoryDTO inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Check stock availability for a product
     */
    @PostMapping("/check-stock")
    public ResponseEntity<Boolean> checkStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/check-stock - Product: {}, Quantity: {}", productId, quantity);
        boolean hasStock = inventoryService.checkStock(productId, quantity);
        return ResponseEntity.ok(hasStock);
    }

    /**
     * Reserve stock for an order
     */
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/reserve - Product: {}, Quantity: {}", productId, quantity);
        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Release reserved stock
     */
    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/release - Product: {}, Quantity: {}", productId, quantity);
        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Confirm reserved inventory
     */
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReservation(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/confirm - Product: {}, Quantity: {}", productId, quantity);
        inventoryService.confirmReservation(productId, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Update inventory (admin operation)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long productId,
            @RequestParam Integer availableStock) {
        log.info("PUT /api/inventory/{} - New Stock: {}", productId, availableStock);
        InventoryDTO updated = inventoryService.updateInventory(productId, availableStock);
        return ResponseEntity.ok(updated);
    }

    /**
     * Create new inventory entry
     */
    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(
            @RequestParam Long productId,
            @RequestParam Integer availableStock) {
        log.info("POST /api/inventory - Product: {}, Stock: {}", productId, availableStock);
        InventoryDTO created = inventoryService.createInventory(productId, availableStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all inventory items
     */
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventory() {
        log.info("GET /api/inventory");
        List<InventoryDTO> all = inventoryService.getAllInventory();
        return ResponseEntity.ok(all);
    }

    /**
     * Get low stock items
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems(
            @RequestParam(defaultValue = "10") Integer threshold) {
        log.info("GET /api/inventory/low-stock - Threshold: {}", threshold);
        List<InventoryDTO> lowStock = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(lowStock);
    }
}